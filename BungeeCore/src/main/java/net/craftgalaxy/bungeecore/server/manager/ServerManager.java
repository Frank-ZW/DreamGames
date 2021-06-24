package net.craftgalaxy.bungeecore.server.manager;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterables;
import net.craftgalaxy.bungeecore.BungeeCore;
import net.craftgalaxy.minigameservice.bungee.StringUtil;
import net.craftgalaxy.bungeecore.config.ConfigurationManager;
import net.craftgalaxy.minigameservice.packet.client.PacketPlayOutConfirmDisconnect;
import net.craftgalaxy.minigameservice.packet.client.PacketPlayOutCreateMinigame;
import net.craftgalaxy.minigameservice.packet.client.PacketPlayOutPromptDisconnect;
import net.craftgalaxy.bungeecore.player.PlayerData;
import net.craftgalaxy.bungeecore.server.ServerData;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerManager {

    private final BungeeCore plugin;
    private final Random random = new Random();
    private final AtomicInteger serverIds = new AtomicInteger();
    private final Map<ServerData.ServerType, Map<String, ServerData>> servers = new HashMap<>();
    private final Map<String, FutureTask<Void>> futureTasks = new HashMap<>();
    private final Queue<ServerData> inactives = new ConcurrentLinkedQueue<>();
    private final Map<ServerData.Minigames, Map<Integer, Set<ServerData>>> queued = new HashMap<>();
    private final Set<ServerData> staging = new HashSet<>();
    private final Set<ServerData> actives = new HashSet<>();
    private final BiMap<Integer, ServerData> gameKeys = HashBiMap.create();
    private ServerSocket serverSocket;
    private TaskScheduler scheduler;
    private boolean finished;

    private static ServerManager instance;

    public ServerManager(BungeeCore plugin) {
        this.plugin = plugin;
        this.scheduler = plugin.getProxy().getScheduler();
        this.scheduler.runAsync(plugin, () -> {
            try {
                this.serverSocket = new ServerSocket(ConfigurationManager.getInstance().getPort());
                while (!this.finished) {
                    ServerData serverData = new ServerData(plugin, this.serverSocket.accept(), this.serverIds.incrementAndGet());
                    FutureTask<Void> future = new FutureTask<>(serverData, null);
                    this.futureTasks.put(serverData.getServerName(), future);
                    this.scheduler.runAsync(plugin, future);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    this.serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void enable(BungeeCore plugin) {
        instance = new ServerManager(plugin);
    }

    public static void disable() {
        if (instance == null) {
            return;
        }

        instance.finished = true;
        instance.scheduler.cancel(instance.plugin);
        for (Map<String, ServerData> value : instance.servers.values()) {
            for (ServerData serverData : value.values()) {
                try {
                    serverData.sendPacket(new PacketPlayOutPromptDisconnect(true));
                } catch (IOException e) {
                    instance.plugin.getLogger().warning("Failed to send disconnection packet to " + serverData.getServerName() + ". This server may not shutdown properly.");
                }
            }
        }

        Iterator<FutureTask<Void>> iterator = instance.futureTasks.values().iterator();
        while (iterator.hasNext()) {
            FutureTask<Void> future = iterator.next();
            try {
                future.get(8, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            } finally {
                iterator.remove();
            }
        }

        instance.futureTasks.clear();
        instance.servers.clear();
        instance.inactives.clear();
        instance.queued.clear();
        instance.staging.clear();
        instance.actives.clear();
        instance.gameKeys.clear();
        instance.scheduler = null;
        instance.serverSocket = null;
        instance = null;
    }

    public static ServerManager getInstance() {
        return instance;
    }

    public ServerData getRandomLobby() {
        Map<String, ServerData> lobbies = this.servers.get(ServerData.ServerType.LOBBY);
        if (lobbies == null || lobbies.isEmpty()) {
            throw new IllegalStateException("There are no minigame lobbies connected to the proxy.");
        } else {
            return Iterables.get(lobbies.values(), this.random.nextInt(lobbies.size()));
        }
    }

    @Nullable
    public ServerData getServerData(String name) {
        for (Map<String, ServerData> value : this.servers.values()) {
            ServerData serverData = value.get(name);
            if (serverData != null) {
                return serverData;
            }
        }

        return null;
    }

    @Nullable
    public ServerData getServerData(@NotNull ServerInfo server) {
        return this.getServerData(server.getName());
    }

    public void connectServer(@NotNull String name, @NotNull ServerData serverData) {
        serverData.setServerType(ConfigurationManager.getInstance().isLobby(name) ? ServerData.ServerType.LOBBY : (ConfigurationManager.getInstance().isHub(name) ? ServerData.ServerType.HUB : (ConfigurationManager.getInstance().isMinigame(name) ? ServerData.ServerType.MINIGAME : ServerData.ServerType.INACTIVE)));
        switch (serverData.getServerType()) {
            case INACTIVE:
                this.plugin.getLogger().info(ChatColor.RED + "The proxy received an unknown TCP socket request for " + name + ". Please update the proxy config.yml and restart both the backend server and the proxy.");
                return;
            case MINIGAME:
                this.inactives.add(serverData);
            default:
                this.servers.computeIfAbsent(serverData.getServerType(), ignored -> new HashMap<>()).put(name, serverData);
                this.plugin.getLogger().info(ChatColor.GREEN + "Established TCP socket connection for " + (serverData.getServerType() == ServerData.ServerType.MINIGAME ? "the minigame" : "the lobby") + " server " + name + " with Server-ID " + serverData.getServerId() + ". This server can now communicate with the proxy.");
        }
    }

    public void disconnectServer(@NotNull ServerData serverData) {
        FutureTask<Void> future = this.futureTasks.remove(serverData.getServerName());
        if (future != null) {
            future.cancel(false);
        }

        this.servers.computeIfPresent(serverData.getServerType(), (type, result) -> {
            result.remove(serverData.getServerName());
            return result;
        });
        this.inactives.remove(serverData);
        try {
            serverData.sendPacket(new PacketPlayOutConfirmDisconnect());
            this.plugin.getLogger().info(ChatColor.GREEN + "Interrupted TCP socket connection for " + serverData.getServerName() + ". This server will no longer receive data from the proxy.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearFromQueued(@NotNull ServerData serverData) {
        for (Map<Integer, Set<ServerData>> minigames : this.queued.values()) {
            for (Set<ServerData> servers : minigames.values()) {
                servers.remove(serverData);
            }
        }
    }

    public void queueServer(@NotNull ServerData serverData, boolean reset) {
        if (reset) {
            this.actives.remove(serverData);
            this.gameKeys.inverse().remove(serverData);
            this.staging.remove(serverData);
            this.clearFromQueued(serverData);
            this.inactives.add(serverData.reset());
            return;
        }

        this.queued.computeIfAbsent(serverData.getMinigame(), ignored -> new HashMap<>()).computeIfAbsent(serverData.getMaxPlayers(), ignored -> new HashSet<>()).add(serverData);
    }

    public ServerData.Minigames checkMinigame(@NotNull ProxiedPlayer sender, String name, int maxPlayers) {
        boolean valid;
        ServerData.Minigames minigame;
        switch (name.toLowerCase()) {
            case "manhunt":
                minigame = ServerData.Minigames.MANHUNT;
                valid = maxPlayers == 1 ? sender.hasPermission(String.format(StringUtil.SOLO_COMMAND_PERMISSION, "manhunt")) : maxPlayers >= 2 && maxPlayers <= 5;
                break;
            case "deathswap":
                minigame = ServerData.Minigames.INACTIVE;
                valid = false;
                break;
            default:
                sender.sendMessage(new TextComponent(ChatColor.RED + "That minigame has not been added to the server. To help the server grow, consider making a small donation through our online web-store."));
                return ServerData.Minigames.INACTIVE;
        }

        if (!valid) {
            sender.sendMessage(new TextComponent(ChatColor.RED + minigame.getDisplayName() + "s with " + maxPlayers + " player are currently unsupported. To help the developer, consider making a small donation through our online web-store."));
            return ServerData.Minigames.INACTIVE;
        }

        return minigame;
    }

    public void queuePlayer(@NotNull PlayerData senderData, @NotNull String name, int maxPlayers) {
        ProxiedPlayer sender = senderData.getPlayer();
        ServerData.Minigames type = this.checkMinigame(sender, name, maxPlayers);
        if (type == ServerData.Minigames.INACTIVE) {
            return;
        }

        ServerData serverData;
        Set<ServerData> servers = this.queued.computeIfAbsent(type, ignored -> new HashMap<>()).computeIfAbsent(maxPlayers, ignored -> new HashSet<>());
        if (servers.isEmpty()) {
            serverData = this.inactives.poll();
            if (serverData == null) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "There are currently no available servers to host a " + type.getDisplayName() + " on. Please try again later."));
                return;
            }

            try {
                serverData.sendPacket(new PacketPlayOutCreateMinigame(type.ordinal(), ConfigurationManager.getInstance().getThenIncrementGameKey(), maxPlayers));
                serverData.setMinigame(type);
                serverData.setMaxPlayers(maxPlayers);
                serverData.setPlayers(0);
                servers.add(serverData);
            } catch (IOException e) {
                e.printStackTrace();
                sender.sendMessage(new TextComponent(ChatColor.RED + "An error occurred while creating a " + type.getDisplayName() + " game. Contact an administrator if this occurs."));
                return;
            }
        } else {
            serverData = Iterables.get(servers, this.random.nextInt(servers.size()));
        }

        if (serverData.incrementPlayers() >= maxPlayers) {
            servers.remove(serverData);
            this.staging.add(serverData);
        }

        serverData.sendQueuedPlayer(sender);
    }

    public void addActiveServer(@NotNull ServerData serverData, int gameKey) {
        if (!this.staging.remove(serverData)) {
            this.inactives.remove(serverData);
            this.gameKeys.inverse().remove(serverData);
            this.actives.remove(serverData);
            this.clearFromQueued(serverData);
        }

        this.gameKeys.put(gameKey, serverData);
        this.actives.add(serverData);
        this.plugin.getLogger().info(ChatColor.GREEN + "Starting " + serverData.getMinigame().getDisplayName() + " on " + serverData.getServerName() + " with game key " + gameKey);
    }

    public void addInactiveServer(@NotNull ServerData serverData) {
        if (!this.actives.remove(serverData)) {
            this.staging.remove(serverData);
            this.clearFromQueued(serverData);
        }

        this.gameKeys.inverse().remove(serverData);
        this.inactives.add(serverData.reset());
        this.plugin.getLogger().info(ChatColor.GREEN + "Re-added " + serverData.getServerName() + " to the inactive server queue.");
    }
}
