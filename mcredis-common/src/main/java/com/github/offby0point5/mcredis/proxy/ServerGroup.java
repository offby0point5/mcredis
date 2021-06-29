package com.github.offby0point5.mcredis.proxy;

import com.github.offby0point5.mcredis.datatype.ItemStack;
import com.github.offby0point5.mcredis.Group;
import com.github.offby0point5.mcredis.rules.JoinRules;
import com.github.offby0point5.mcredis.rules.KickRules;

import java.util.Set;

/**
 * Represents a server group in the network. Used to gather and cache data.
 */
public class ServerGroup {
    private long lastUpdate = 0;
    protected final Group currentData;

    protected final String groupName;
    protected ItemStack item;
    protected JoinRules joinRule;
    protected KickRules kickRule;
    protected Set<String> memberServers;

    public ServerGroup(String groupName) {
        this.groupName = groupName;
        this.currentData = new Group(groupName);
    }

    public String getGroupName() {
        return groupName;
    }

    public ItemStack getItem() {
        update();
        return item;
    }

    public Set<String> getMemberServers() {
        update();
        return memberServers;
    }

    public JoinRules getJoinRule() {
        update();
        return joinRule;
    }

    public KickRules getKickRule() {
        update();
        return kickRule;
    }

    public void update() {
        final long timestamp = System.currentTimeMillis();
        if (timestamp - lastUpdate < 2000) return;  // do not update faster than every 2 seconds
        lastUpdate = timestamp;

        this.item = this.currentData.getItem();
        this.joinRule = this.currentData.getJoinRule();
        this.kickRule = this.currentData.getKickRule();
        this.memberServers = this.currentData.getMembers();
    }
}
