package com.github.offby0point5.mcredis.proxy;

import com.github.offby0point5.mcredis.Server;

import java.net.InetSocketAddress;
import java.util.Set;

/**
 * Represents a single server in the network. Used to gather and cache data.
 */
public class SingleServer {
    private long lastUpdate = 0;
    protected final Server currentData;

    protected final String serverName;
    protected InetSocketAddress address;
    protected Server.ServerOnlineStatus status;
    protected String mainGroup;
    protected Set<String> allGroups;

    public SingleServer(String serverName) {
        this.currentData = new Server(serverName);
        this.serverName = serverName;
    }

    public String getServerName() {
        return serverName;
    }

    public Server.ServerOnlineStatus getStatus() {
        update();
        return status;
    }

    public String getMainGroup() {
        update();
        return mainGroup;
    }

    public Set<String> getAllGroups() {
        update();
        return allGroups;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public void update() {
        final long timestamp = System.currentTimeMillis();
        if (timestamp - lastUpdate < 2000) return;  // do not update faster than every 2 seconds
        lastUpdate = timestamp;

        this.status = this.currentData.getStatus();
        this.mainGroup = this.currentData.getMain();
        this.allGroups = this.currentData.getGroups();
        this.address = this.currentData.getAddress();
    }
}

