package com.github.offby0point5.mcredis;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import java.io.File;
import java.net.URL;

public class Configuration {
    public static final String REDIS_HOST = "redis.host";
    public static final String REDIS_PORT = "redis.port";

    public static final String REDIS_USER = "redis.user";
    public static final String REDIS_PASS = "redis.pass";

    private static CommentedFileConfig config;

    protected static ConfigSpec configSpec;

    static {
        configSpec = new ConfigSpec();
        configSpec.defineOfClass(REDIS_HOST, "localhost", String.class);
        configSpec.defineInRange(REDIS_PORT, 6379, 1000, 65535);

        configSpec.defineOfClass(REDIS_USER, "", String.class);
        configSpec.defineOfClass(REDIS_PASS, "", String.class);
    }

    /**
     * Loads and validates the config file
     */
    public static void reload() {
        URL defaultConfigLocation = Configuration.class.getClassLoader().getResource("redis-server.toml");
        if (defaultConfigLocation == null) {
            throw new RuntimeException("Default configuration file does not exist.");
        }

        config = CommentedFileConfig.builder(new File("redis-server.toml"))
                .defaultData(defaultConfigLocation)
                .preserveInsertionOrder()
                .sync()
                .build();
        config.load();
        boolean needSave = !configSpec.isCorrect(config);
        configSpec.correct(config, (action, path, incorrectValue, correctedValue)
                -> System.out.printf("%s %s %s %s%n", action, path, incorrectValue, correctedValue));
        if (needSave) config.save();
        config.close();
    }

    // ==== OPTION GETTERS ==================================================
    public static String getRedisHost() {
        return config.getOrElse(REDIS_HOST, "localhost");
    }

    public static int getRedisPort() {
        return config.getOrElse(REDIS_PORT, 6379);
    }

    public static String getRedisUser() {
        return config.getOrElse(REDIS_USER, "");
    }

    public static String getRedisPass() {
        return config.getOrElse(REDIS_PASS, "");
    }
}
