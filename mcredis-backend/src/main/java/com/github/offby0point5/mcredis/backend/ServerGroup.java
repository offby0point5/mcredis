package com.github.offby0point5.mcredis.backend;

import com.github.offby0point5.mcredis.Group;
import com.github.offby0point5.mcredis.datatype.ItemStack;

import java.util.Set;

/**
 * Represents a server group in the network. Used to gather and cache data.
 */
public class ServerGroup {
    private long lastUpdate = 0;
    protected final Group currentData;

    protected final String groupName;
    protected ItemStack item;
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

    public void update() {
        final long timestamp = System.currentTimeMillis();
        if (timestamp - lastUpdate < 2000) return;  // do not update faster than every 2 seconds
        lastUpdate = timestamp;

        this.item = this.currentData.getItem();
        this.memberServers = this.currentData.getMembers();
    }
}
