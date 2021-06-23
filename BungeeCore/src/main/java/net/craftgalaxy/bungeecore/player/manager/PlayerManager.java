package net.craftgalaxy.bungeecore.player.manager;

import net.craftgalaxy.bungeecore.BungeeCore;
import net.craftgalaxy.bungeecore.player.PlayerData;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {

    private BungeeCore plugin;
    private final Map<UUID, PlayerData> players = new HashMap<>();
    private final Map<UUID, ServerInfo> disconnections = new HashMap<>();
    private static PlayerManager instance;

    public PlayerManager(BungeeCore plugin) {
        this.plugin = plugin;
        plugin.getProxy().getPlayers().forEach(this::addPlayer);
    }

    public static void enable(BungeeCore plugin) {
        instance = new PlayerManager(plugin);
    }

    public static void disable() {
        if (instance == null) {
            return;
        }

        instance.plugin.getProxy().getPlayers().forEach(instance::removePlayer);
        instance.disconnections.clear();
        instance.plugin = null;
        instance = null;
    }

    public static PlayerManager getInstance() {
        return instance;
    }

    public void removeDisconnections(Collection<UUID> players) {
        this.disconnections.keySet().removeAll(players);
    }

    public void removeDisconnections(ServerInfo server) {
        this.disconnections.values().remove(server);
    }

    @Nullable
    public ServerInfo removeDisconnection(UUID uniqueId) {
        return this.disconnections.remove(uniqueId);
    }

    public void addPlayer(ProxiedPlayer player) {
        this.players.put(player.getUniqueId(), new PlayerData(player));
    }

    public void removePlayer(ProxiedPlayer player) {
        PlayerData playerData = this.players.remove(player.getUniqueId());
        if (playerData != null && playerData.isPlaying()) {
            this.disconnections.put(player.getUniqueId(), player.getServer().getInfo());
        }
    }

    @Nullable
    public PlayerData getPlayerData(UUID uniqueId) {
        return this.players.get(uniqueId);
    }
}
