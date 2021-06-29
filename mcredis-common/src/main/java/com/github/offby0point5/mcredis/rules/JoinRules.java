package com.github.offby0point5.mcredis.rules;

import com.github.offby0point5.mcredis.Group;
import com.github.offby0point5.mcredis.Player;
import com.github.offby0point5.mcredis.Server;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public enum JoinRules {
    NONE(((player, groupJoined) -> null)),
    RANDOM((player, groupJoined) -> {
        List<Server> servers = groupJoined.getMembers().stream().map(Server::new)
                .filter(server -> Server.canJoin(server.getStatus()))
                .collect(Collectors.toList());
        return servers.get(new Random().nextInt(servers.size()));
    }),
    LEAST((player, groupJoined) -> {
        List<Server> servers = groupJoined.getMembers().stream().map(Server::new)
                .filter(server -> Server.canJoin(server.getStatus()))
                .collect(Collectors.toList());
        int minPlayers = Integer.MAX_VALUE;
        Server returnServer = null;
        for (Server server : servers) {
            int playerNum = server.getPlayers().size();
            if (playerNum < minPlayers) {
                returnServer = server;
                minPlayers = playerNum;
            }
        }
        return returnServer;
    }),
    MOST((player, groupJoined) -> {
        List<Server> servers = groupJoined.getMembers().stream().map(Server::new)
                .filter(server -> Server.canJoin(server.getStatus()))
                .collect(Collectors.toList());
        int maxPlayers = Integer.MIN_VALUE;
        Server returnServer = null;
        for (Server server : servers) {
            int playerNum = server.getPlayers().size();
            if (playerNum > maxPlayers) {
                returnServer = server;
                maxPlayers = playerNum;
            }
        }
        return returnServer;
    }),
    ;

    private final ServerGroupJoinRule rule;

    JoinRules(ServerGroupJoinRule joinRule) {
        rule = joinRule;
    }

    public Server getJoinServer(Player player, Group groupJoined) {
        return rule.getJoinServer(player, groupJoined);
    }

    private interface ServerGroupJoinRule {
        Server getJoinServer(Player player, Group groupJoined);
    }
}
