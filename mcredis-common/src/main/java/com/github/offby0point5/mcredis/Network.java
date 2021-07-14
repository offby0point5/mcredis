package com.github.offby0point5.mcredis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Network {
    private static final JedisPool JEDIS_POOL;
    public static final String NETWORK_PREFIX = "mcn";

    static {
        Configuration.reload();
        if (Configuration.getRedisUser().isBlank() || Configuration.getRedisPass().isBlank()) {
            JEDIS_POOL = new JedisPool(new JedisPoolConfig(),
                    Configuration.getRedisHost(), Configuration.getRedisPort());
        } else {
            JEDIS_POOL = new JedisPool(new JedisPoolConfig(),
                    Configuration.getRedisHost(), Configuration.getRedisPort(),
                    Configuration.getRedisUser(), Configuration.getRedisPass());
        }
    }

    public static Jedis getJedis() {
        return JEDIS_POOL.getResource();
    }

    public static Set<String> getServers() {
        try (Jedis jedis = getJedis()) {
            return jedis.keys(Server.PREFIX+":*")
                    .stream()
                    .map(s -> s.substring(Server.PREFIX.length()+1))
                    .map(s -> s.substring(0, s.indexOf(":")))
                    .collect(Collectors.toSet());
        }
    }

    public static Set<String> getGroups() {
        try (Jedis jedis = getJedis()) {
            return jedis.keys(Group.PREFIX+":*")
                    .stream()
                    .map(s -> s.substring(Group.PREFIX.length()+1))
                    .map(s -> s.substring(0, s.indexOf(":")))
                    .collect(Collectors.toSet());
        }
    }

    public static Set<UUID> getPlayers() {
        try (Jedis jedis = getJedis()) {
            return jedis.keys(Player.PREFIX+":*")
                    .stream()
                    .map(s -> s.substring(Player.PREFIX.length()+1))
                    .map(s -> s.substring(0, s.indexOf(":")))
                    .map(UUID::fromString)
                    .collect(Collectors.toSet());
        }
    }

    public static Set<UUID> getParties() {
        try (Jedis jedis = getJedis()) {
            return jedis.keys(Party.PREFIX+":*")
                    .stream()
                    .map(s -> s.substring(Party.PREFIX.length()+1))
                    .map(s -> s.substring(0, s.indexOf(":")))
                    .map(UUID::fromString)
                    .collect(Collectors.toSet());
        }
    }
}
