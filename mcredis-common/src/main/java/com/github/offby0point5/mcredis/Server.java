package com.github.offby0point5.mcredis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.stream.Collectors;

public class Server {
    protected static final String PREFIX = String.format("%s:server", Network.NETWORK_PREFIX);

    private final String name;

    protected final String ADDRESS;
    protected final String STATUS;
    protected final String MAIN_GROUP;
    protected final String ALL_GROUPS;
    protected final String PLAYERS;

    public Server(String serverName) {
        Objects.requireNonNull(serverName);
        ADDRESS = String.format("%s:%s:address", PREFIX, serverName);
        STATUS = String.format("%s:%s:status", PREFIX, serverName);
        MAIN_GROUP = String.format("%s:%s:main", PREFIX, serverName);
        ALL_GROUPS = String.format("%s:%s:groups", PREFIX, serverName);
        PLAYERS = String.format("%s:%s:players", PREFIX, serverName);
        name = serverName;
    }

    public String getName() {
        return name;
    }

    public InetSocketAddress getAddress() {
        try (Jedis jedis = Network.getJedis()) {
            String rawAddress = jedis.get(ADDRESS);
            if (rawAddress == null) return null;
            String[] inetAddr = rawAddress.split(":");
            return new InetSocketAddress(inetAddr[0], Integer.parseInt(inetAddr[1]));
        }
    }

    public void setAddress(InetSocketAddress serverAddress) {
        try (Jedis jedis = Network.getJedis()) {
            jedis.set(ADDRESS, String.format("%s:%d", serverAddress.getHostString(), serverAddress.getPort()));
        }
    }

    public ServerOnlineStatus getStatus() {
        try (Jedis jedis = Network.getJedis()){
            String statueName = jedis.get(STATUS);
            if (statueName == null) return null;
            return ServerOnlineStatus.valueOf(statueName);
        }
    }

    public void setStatus(ServerOnlineStatus serverStatus) {
        try (Jedis jedis = Network.getJedis()){
            jedis.set(STATUS, serverStatus.name());
        }
    }

    public String getMain() {
        try (Jedis jedis = Network.getJedis()){
            return jedis.get(MAIN_GROUP);
        }
    }

    public void setMain(String groupName) {
        try (Jedis jedis = Network.getJedis()){
            Transaction transaction = jedis.multi();
            transaction.set(MAIN_GROUP, groupName);

            String oldMain = getMain();
            if (oldMain != null) {
                Group oldGroup = new Group(oldMain);
                transaction.srem(oldGroup.MEMBERS, name);
            }

            Group newGroup = new Group(groupName);
            transaction.sadd(newGroup.MEMBERS, name);
            transaction.exec();
        }
    }

    public Set<String> getGroups() {
        try (Jedis jedis = Network.getJedis()){
            return jedis.smembers(ALL_GROUPS);
        }
    }

    public void addGroups(String... groupNames) {
        try (Jedis jedis = Network.getJedis()){
            Transaction transaction = jedis.multi();
            transaction.sadd(ALL_GROUPS, groupNames);
            for (String groupName : groupNames) {
                Group group = new Group(groupName);
                transaction.sadd(group.MEMBERS, name);
            }
            transaction.exec();
        }
    }

    public void remGroups(String... groupNames) {
        try (Jedis jedis = Network.getJedis()){
            Transaction transaction = jedis.multi();
            transaction.srem(ALL_GROUPS, groupNames);
            for (String groupName : groupNames) {
                Group group = new Group(groupName);
                transaction.srem(group.MEMBERS, name);
            }
            transaction.exec();
        }
    }

    public Set<UUID> getPlayers() {
        try (Jedis jedis = Network.getJedis()){
            return jedis.smembers(PLAYERS).stream().map(UUID::fromString).collect(Collectors.toSet());
        }
    }

    public void delete() {
        try (Jedis jedis = Network.getJedis()) {
            Transaction transaction = jedis.multi();
            for (String groupName : getGroups()) {
                Group group = new Group(groupName);
                transaction.srem(group.MEMBERS, name);
            }
            String main = getMain();
            if (main != null) {
                Group group = new Group(main);
                transaction.srem(group.MEMBERS, name);
            }
            transaction.del(ADDRESS);
            transaction.del(STATUS);
            transaction.del(MAIN_GROUP);
            transaction.del(ALL_GROUPS);
            for (UUID playerId : getPlayers()) {
                Player player = new Player(playerId);
                transaction.del(player.SERVER);
            }
            transaction.del(PLAYERS);
            transaction.exec();
        }
    }

    public static boolean canJoin(ServerOnlineStatus status) {
        switch (status) {
            case ONLINE:
            case SOFT_FULL:
                return true;
            case HARD_FULL:
            case OFFLINE:
                return false;
        }
        return false;
    }

    public enum ServerOnlineStatus {
        ONLINE,
        SOFT_FULL,
        HARD_FULL,
        OFFLINE
    }
}
