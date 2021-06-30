package com.github.offby0point5.mcredis.proxy;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class SendUtil {
    public static SendRequest decodeMessage(byte[] message) {
        Objects.requireNonNull(message);
        SendRequest.SendType type;
        if (message[0] == 0) type = SendRequest.SendType.SERVER;
        else type = SendRequest.SendType.GROUP;
        String target = new String(message, StandardCharsets.US_ASCII);
        return new SendRequest(type, target);
    }

    public static class SendRequest {
        public final SendType type;
        public final String target;

        SendRequest(SendType type, String target) {
            this.type = type;
            this.target = target;
        }

        public enum SendType {
            SERVER,
            GROUP
        }
    }
}
