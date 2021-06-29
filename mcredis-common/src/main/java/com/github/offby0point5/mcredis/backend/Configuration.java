package com.github.offby0point5.mcredis.backend;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.github.offby0point5.mcredis.rules.JoinRules;
import com.github.offby0point5.mcredis.rules.KickRules;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Configuration {
    public static final String SERVER_MAIN = "server.main";
    public static final String SERVER_GROUPS = "server.groups";

    public static final String SERVER_DEFAULT_JOIN = "group.join";
    public static final String SERVER_DEFAULT_KICK = "group.kick";

    public static final String SERVER_DEFAULT_ITEM_NAME = "group.item.name";
    public static final String SERVER_DEFAULT_ITEM_MATERIAL = "group.item.material";
    public static final String SERVER_DEFAULT_ITEM_AMOUNT = "group.item.amount";
    public static final String SERVER_DEFAULT_ITEM_GLOWING = "group.item.glowing";
    public static final String SERVER_DEFAULT_ITEM_LORE = "group.item.lore";

    private static CommentedFileConfig config;

    protected static ConfigSpec configSpec;

    private static File configFile;
    private static final String serverName;
    private static GetInteger getServerPort;
    private static GetString getServerHost;

    static {
        // Server name is a random 8 character string
        byte[] array = new byte[8]; new Random().nextBytes(array);
        serverName = new String(array, StandardCharsets.UTF_8);

        configSpec = new ConfigSpec();
        configSpec.defineOfClass(SERVER_MAIN, "lobby", String.class);
        configSpec.defineList(SERVER_GROUPS, Collections.emptyList(), e -> e instanceof String);

        configSpec.defineOfClass(SERVER_DEFAULT_JOIN, "LEAST", String.class);
        configSpec.defineOfClass(SERVER_DEFAULT_KICK, "LOBBY", String.class);

        configSpec.defineOfClass(SERVER_DEFAULT_ITEM_NAME, "Lobby", String.class);
        configSpec.defineOfClass(SERVER_DEFAULT_ITEM_MATERIAL, "BIRCH_SAPLING", String.class);
        configSpec.defineOfClass(SERVER_DEFAULT_ITEM_GLOWING, false, Boolean.class);
        configSpec.defineList(SERVER_DEFAULT_ITEM_LORE, Collections.emptyList(), s -> s instanceof String);
        configSpec.defineInRange(SERVER_DEFAULT_ITEM_AMOUNT, 1, 1, 64);
    }

    public static void setup(File configurationFile, GetString serverHost, GetInteger serverPort) {
        configFile = configurationFile;
        getServerHost = serverHost;
        getServerPort = serverPort;
    }

    /**
     * Loads and validates the config file
     */
    public static void reload() {
        URL defaultConfigLocation = Configuration.class.getClassLoader().getResource("minecraft-redis.toml");
        if (defaultConfigLocation == null) {
            throw new RuntimeException("Default configuration file does not exist.");
        }

        config = CommentedFileConfig.builder(configFile)
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

    public interface GetInteger { int run();}
    public interface GetString { String run();}

    // ==== OPTION GETTERS ==================================================
    public static String getServerId() {
        return String.format("%s-%d", serverName, getServerPort());
    }

    public static int getServerPort() {
        return getServerPort.run();
    }

    public static String getServerHost() {
        return getServerHost.run();
    }

    public static String getServerMain() {
        return config.getOrElse(SERVER_MAIN, "lobby");
    }

    public static Set<String> getServerGroups() {
        return new HashSet<>(config.getOrElse(SERVER_GROUPS, Collections.emptyList()));
    }

    public static JoinRules getServerDefaultJoin() {
        return JoinRules.valueOf(config.getOrElse(SERVER_DEFAULT_JOIN, "LEAST"));
    }

    public static KickRules getServerDefaultKick() {
        return KickRules.valueOf(config.getOrElse(SERVER_DEFAULT_KICK, "LOBBY"));
    }

    public static String getServerDefaultItemName() {
        return config.getOrElse(SERVER_DEFAULT_ITEM_NAME, serverName);
    }

    public static String getServerDefaultItemMaterial() {
        return config.getOrElse(SERVER_DEFAULT_ITEM_MATERIAL, "BIRCH_SAPLING");
    }

    public static int getServerDefaultItemAmount() {
        return config.getOrElse(SERVER_DEFAULT_ITEM_AMOUNT, 1);
    }

    public static boolean getServerDefaultItemGlowing() {
        return config.getOrElse(SERVER_DEFAULT_ITEM_GLOWING, false);
    }

    public static List<String> getServerDefaultItemLore() {
        return config.getOrElse(SERVER_DEFAULT_ITEM_LORE, Collections.emptyList());
    }
}
