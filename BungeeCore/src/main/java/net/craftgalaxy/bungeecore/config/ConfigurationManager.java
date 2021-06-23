package net.craftgalaxy.bungeecore.config;

import net.craftgalaxy.bungeecore.BungeeCore;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public class ConfigurationManager {

    private BungeeCore plugin;
    private File file;
    private Configuration config;
    private List<String> hubs;
    private List<String> lobbies;
    private List<String> minigames;
    private AtomicInteger gameKey;
    private int port;
    private boolean connected;
    private static ConfigurationManager instance;

    public ConfigurationManager(BungeeCore plugin) {
        this.plugin = plugin;
        if (!plugin.getDataFolder().exists()) {
            if (!plugin.getDataFolder().mkdir()) {
                plugin.getLogger().severe("Failed to create main plugin directory...");
                return;
            }
        }

        this.file = new File(plugin.getDataFolder(), "config.yml");
        if (!this.file.exists()) {
            try (InputStream input = plugin.getResourceAsStream("config.yml")) {
                Files.copy(input, this.file.toPath());
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to create a new instance of config.yml file... is the plugin allowed to read and write to files?", e);
                return;
            }
        }

        try {
            this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(this.file);
            this.hubs = this.config.getStringList("server-settings.lobbies.hub");
            this.lobbies = this.config.getStringList("server-settings.lobbies.minigame");
            this.minigames = this.config.getStringList("server-settings.minigames");
            this.gameKey = new AtomicInteger(this.config.getInt("latest-game-key"));
            this.port = this.config.getInt("socket-settings.port");
            this.connected = true;
        } catch (Exception e) {
            if (e instanceof NumberFormatException) {
                plugin.getLogger().log(Level.WARNING, "Failed to read in one or more values entered in the config.yml file. Before reloading the plugin, ensure that all values entered are of the correct data type.", e);
            } else {
                plugin.getLogger().log(Level.SEVERE, "An error occurred while creating the config.yml file", e);
            }
        }
    }

    public static void enable(BungeeCore plugin) {
        instance = new ConfigurationManager(plugin);
    }

    public static void disable() {
        if (instance == null) {
            return;
        }

        instance.config.set("server-settings.lobbies.hub", instance.hubs);
        instance.config.set("server-settings.lobbies.minigame", instance.lobbies);
        instance.config.set("server-settings.minigames", instance.minigames);
        instance.config.set("latest-game-key", instance.gameKey.get());
        instance.config.set("socket-settings.port", instance.port);
        instance.saveConfig();
        instance.hubs.clear();
        instance.lobbies.clear();
        instance.minigames.clear();
        instance.file = null;
        instance.config = null;
        instance.hubs = null;
        instance.lobbies = null;
        instance.minigames = null;
        instance.gameKey = null;
        instance.connected = false;
        instance.plugin = null;
        instance = null;
    }

    public static ConfigurationManager getInstance() {
        return instance;
    }

    public void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(this.config, this.file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to write information to the config", e);
        }
    }

    public boolean isConnected() {
        return this.connected;
    }

    public int getThenIncrementGameKey() {
        return this.gameKey.getAndIncrement();
    }

    public int getPort() {
        return this.port;
    }

    public boolean isHub(String name) {
        return this.hubs.contains(name);
    }

    public boolean isLobby(String name) {
        return this.lobbies.contains(name);
    }

    public boolean isMinigame(String name) {
        return this.minigames.contains(name);
    }
}
