package com.github.offby0point5.mcredis.backend;

import com.github.offby0point5.mcredis.datatype.ItemStack;
import com.github.offby0point5.mcredis.Group;
import com.github.offby0point5.mcredis.Server;
import com.github.offby0point5.mcredis.rules.JoinRules;
import com.github.offby0point5.mcredis.rules.KickRules;

import java.net.InetSocketAddress;

/**
 * Represents network as seen by a backend server.
 */
public class Manager {
    private static SingleServer server = null;

    public static void setup(String serverName,
                             InetSocketAddress address,
                             String mainGroup,
                             ItemStack defaultMenuItem,
                             JoinRules defaultJoinRule,
                             KickRules defaultKickRule,
                             String... allGroups) {
        if (server != null) return;
        server = new SingleServer(serverName);
        server.currentData.setAddress(address);

        server.currentData.setMain(mainGroup);
        Group main = new Group(mainGroup);
        if (defaultMenuItem != null) main.defaultItem(defaultMenuItem);
        if (defaultJoinRule != null) main.defaultJoinRule(defaultJoinRule);
        if (defaultKickRule != null) main.defaultKickRule(defaultKickRule);
        server.currentData.addGroups(allGroups);
    }

    public static void setStatus(Server.ServerOnlineStatus status) {
        server.currentData.setStatus(status);
    }

    public static void shutdown() {
        server.currentData.delete();
    }
}
