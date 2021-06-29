package com.github.offby0point5.mcredis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.Objects;
import java.util.UUID;

public class Player {
    protected static final String PREFIX = String.format("%s:player", Network.NETWORK_PREFIX);

    private final UUID uuid;

    protected final String SERVER;
    protected final String PARTY;

    public Player(UUID playerUuid) {
        Objects.requireNonNull(playerUuid);
        SERVER = String.format("%s:%s:server", PREFIX, playerUuid);
        PARTY = String.format("%s:%s:party", PREFIX, playerUuid);
        this.uuid = playerUuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getServer() {
        try (Jedis jedis = Network.getJedis()) {
            return jedis.get(SERVER);
        }
    }

    public UUID getParty() {
        try (Jedis jedis = Network.getJedis()) {
            String partyUUID = jedis.get(PARTY);
            if (partyUUID == null) return null;
            return UUID.fromString(partyUUID);
        }
    }

    public void delete() {
        String serverName = getServer();
        UUID partyId = getParty();
        try (Jedis jedis = Network.getJedis()) {
            Transaction transaction = jedis.multi();
            if (serverName != null) {
                Server server = new Server(getServer());
                transaction.srem(server.PLAYERS, uuid.toString());
            }

            if (partyId != null) {
                Party party = new Party(partyId);
                transaction.srem(party.MEMBERS, partyId.toString());
                if (party.getLeader().equals(uuid))
                    transaction.del(party.LEADER);
            }
            transaction.del(SERVER);
            transaction.del(PARTY);

            transaction.exec();
        }
    }

    public void joinServer(String serverName) {
        try (Jedis jedis = Network.getJedis()) {
            String currentServerName = getServer();
            Transaction transaction = jedis.multi();
            if (currentServerName != null) {
                Server currentServer = new Server(currentServerName);
                transaction.srem(currentServer.PLAYERS, uuid.toString());
            }
            transaction.set(SERVER, serverName);
            Server newServer = new Server(serverName);
            transaction.sadd(newServer.PLAYERS, uuid.toString());
            transaction.exec();
        }
    }

    public void joinParty(UUID partyId) {
        try (Jedis jedis = Network.getJedis()) {
            Party party = new Party(partyId);
            Transaction transaction = jedis.multi();
            transaction.set(PARTY, partyId.toString());
            transaction.sadd(party.MEMBERS, uuid.toString());
            transaction.exec();
        }
    }

    public void leaveParty(UUID partyId) {
        try (Jedis jedis = Network.getJedis()) {
            Party party = new Party(partyId);
            Transaction transaction = jedis.multi();
            transaction.del(PARTY);
            transaction.srem(party.MEMBERS, uuid.toString());
            transaction.exec();
        }
    }
}
