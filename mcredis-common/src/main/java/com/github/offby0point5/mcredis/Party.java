package com.github.offby0point5.mcredis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.*;
import java.util.stream.Collectors;

public class Party {
    protected static final String PREFIX = String.format("%s:player-group", Network.NETWORK_PREFIX);

    private final UUID uuid;

    protected final String LEADER;
    protected final String MEMBERS;

    public Party(UUID groupID) {
        Objects.requireNonNull(groupID);
        LEADER = String.format("%s:%s:leader", PREFIX, groupID);
        MEMBERS = String.format("%s:%s:members", PREFIX, groupID);
        uuid = groupID;
    }

    public UUID getUuid() {
        return uuid;
    }

    public UUID getLeader() {
        try (Jedis jedis = Network.getJedis()) {
            String leaderUUID = jedis.get(LEADER);
            if (leaderUUID == null) return null;
            return UUID.fromString(leaderUUID);
        }
    }

    public Set<UUID> getMembers() {
        try (Jedis jedis = Network.getJedis()) {
            return jedis.smembers(MEMBERS).stream().map(UUID::fromString).collect(Collectors.toSet());
        }
    }

    public void delete() {
        try (Jedis jedis = Network.getJedis()) {
            Transaction transaction = jedis.multi();
            for (UUID playerId : getMembers()) {
                Player player = new Player(playerId);
                transaction.del(player.PARTY);
            }
            UUID leader = getLeader();
            if (leader != null) {
                Player player = new Player(leader);
                transaction.del(player.PARTY);
            }
            transaction.del(LEADER);
            transaction.del(MEMBERS);
            transaction.exec();
        }
    }
}
