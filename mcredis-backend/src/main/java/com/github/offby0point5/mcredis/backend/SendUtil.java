package com.github.offby0point5.mcredis.backend;

import com.github.offby0point5.mcredis.Group;
import com.github.offby0point5.mcredis.Server;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SendUtil {
    public static Byte[] createMessage(Group group) {
        Objects.requireNonNull(group);
        List<Byte> bytes = new ArrayList<>();
        bytes.add((byte) 1);  // send to group
        for (Byte character : group.getName().getBytes(StandardCharsets.US_ASCII)) {  // append target name
            bytes.add(character);
        }
        return bytes.toArray(new Byte[0]);
    }

    public static Byte[] createMessage(Server server) {
        Objects.requireNonNull(server);
        List<Byte> bytes = new ArrayList<>();
        bytes.add((byte) 0);  // send to server
        for (Byte character : server.getName().getBytes(StandardCharsets.US_ASCII)) {  // append target name
            bytes.add(character);
        }
        return bytes.toArray(new Byte[0]);
    }
}
