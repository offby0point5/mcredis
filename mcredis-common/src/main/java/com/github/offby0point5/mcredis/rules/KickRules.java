package com.github.offby0point5.mcredis.rules;

import com.github.offby0point5.mcredis.Group;
import com.github.offby0point5.mcredis.Player;
import com.github.offby0point5.mcredis.Server;

public enum KickRules {
    NONE((player, kickedFrom) -> null),
    LOBBY((player, kickedFrom) -> new Group("lobby")),
    GROUP((player, kickedFrom) -> new Group(kickedFrom.getMain())),
    ;

    private final ServerGroupKickRule rule;

    KickRules(ServerGroupKickRule kickRule) {
        rule = kickRule;
    }

    public Group getNewGroup(Player player, Server kickedFrom) {
        return rule.getNewGroup(player, kickedFrom);
    }

    interface ServerGroupKickRule {
        Group getNewGroup(Player player, Server kickedFrom);
    }
}
