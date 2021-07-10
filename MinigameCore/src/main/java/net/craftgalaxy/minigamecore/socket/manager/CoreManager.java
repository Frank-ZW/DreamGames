package net.craftgalaxy.minigamecore.socket.manager;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.craftgalaxy.deathswap.minigame.DeathSwapMinigame;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import net.craftgalaxy.manhunt.minigame.impl.VanillaManhuntMinigame;
import net.craftgalaxy.minigamecore.MinigameCore;
import net.craftgalaxy.minigameservice.bukkit.chat.GameChatRenderer;
import net.craftgalaxy.minigameservice.bukkit.chat.LobbyChatRenderer;
import net.craftgalaxy.minigameservice.bukkit.event.MinigameEndEvent;
import net.craftgalaxy.minigameservice.bukkit.event.MinigameEvent;
import net.craftgalaxy.minigameservice.bukkit.event.MinigameStartEvent;
import net.craftgalaxy.minigameservice.bukkit.minigame.types.AbstractMinigame;
import net.craftgalaxy.minigameservice.bukkit.util.minecraft.PlayerUtil;
import net.craftgalaxy.minigameservice.bukkit.util.java.StringUtil;
import net.craftgalaxy.minigameservice.packet.client.PacketPlayOutCreateMinigame;
import net.craftgalaxy.minigameservice.packet.client.PacketPlayOutForceEnd;
import net.craftgalaxy.minigameservice.packet.client.PacketPlayOutPromptDisconnect;
import net.craftgalaxy.minigameservice.packet.client.PacketPlayOutQueuePlayer;
import net.craftgalaxy.minigameservice.packet.server.*;
import net.craftgalaxy.minigamecore.runnable.PendingConnectionRunnable;
import net.craftgalaxy.minigamecore.socket.SocketWrapper;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class CoreManager {

    private MinigameCore plugin;
    private final ExecutorService executor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("Core Socket Executor").build());
    private final Map<UUID, BukkitTask> disconnections = new HashMap<>();
    private final Map<UUID, UUID> queuedSpectators = new HashMap<>();
    private final Set<UUID> queuedPlayers = new HashSet<>();
    private Future<Void> future;
    private SocketWrapper socket;
    private AbstractMinigame minigame;
    private int maxPlayers;

    private static CoreManager instance;

    public CoreManager(MinigameCore plugin) {
        this.plugin = plugin;
        new PendingConnectionRunnable(plugin).runTaskTimer(plugin, 0L, 200L);
    }

    public static void enable(MinigameCore plugin) {
        instance = new CoreManager(plugin);
    }

    public static void disable() {
        if (instance == null) {
            return;
        }

        instance.executor.shutdown();
        Bukkit.getScheduler().cancelTasks(instance.plugin);
        if (instance.minigame == null) {
            Bukkit.getLogger().info(ChatColor.GREEN + "Ignoring minigame shutdown since there is no currently active minigame on this server.");
        } else {
            switch (instance.minigame.getStatus()) {
                case IN_PROGRESS:
                    instance.minigame.endMinigame(true);
                    break;
                case FINISHED:
                    instance.minigame.endTeleport();
                    instance.minigame.deleteWorlds(true);
                    break;
                default:
                    if (instance.minigame.worldsLoaded()) {
                        instance.minigame.deleteWorlds(true);
                    }

                    instance.minigame.cancelCountdown();
            }
        }

        if (instance.socket == null || !instance.socket.isConnected()) {
            Bukkit.getLogger().info(ChatColor.GREEN + "Ignoring socket since no connection was established.");
        } else {
            try {
                instance.socket.sendPacket(new PacketPlayInRequestDisconnect(Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toList())));
                instance.future.get(8, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException | IOException e) {
                e.printStackTrace();
            }
        }

        instance.queuedPlayers.clear();
        instance.executor.shutdownNow();
        try {
            if (instance.executor.awaitTermination(8, TimeUnit.SECONDS)) {
                Bukkit.getLogger().info(ChatColor.GREEN + "Core executor successfully terminated.");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            instance.socket = null;
            instance.future = null;
            instance.plugin = null;
            instance = null;
        }
    }

    public static CoreManager getInstance() {
        return instance;
    }

    public boolean searchAvailableConnections() throws IOException {
        this.socket = new SocketWrapper(this.plugin, this);
        this.future = this.executor.submit(this.socket, null);
        return this.socket.isConnected();
    }

    public void sendToProxyLobby(@NotNull Player player) {
        try {
            this.socket.sendPacket(new PacketPlayInPlayerAction(player.getUniqueId(), (byte) 0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void teleportToLobby(@NotNull Player player) {
        player.teleportAsync(this.plugin.getLobbyLocation()).thenAccept(result -> {
            if (!result) {
                player.sendMessage(StringUtil.ERROR_TELEPORTING_TO_LOBBY);
                this.sendToProxyLobby(player);
            }
        });
    }

    public void handlePacket(@NotNull Object object) {
        if (object instanceof PacketPlayOutPromptDisconnect) {
            PacketPlayOutPromptDisconnect packet = (PacketPlayOutPromptDisconnect) object;
            Bukkit.getScheduler().runTask(this.plugin, () -> {
                if (packet.isShutdown()) {
                    Bukkit.getServer().shutdown();
                } else {
                    Bukkit.getPluginManager().disablePlugin(this.plugin);
                }
            });
        } else if (object instanceof PacketPlayOutQueuePlayer) {
            PacketPlayOutQueuePlayer packet = (PacketPlayOutQueuePlayer) object;
            switch (packet.getType()) {
                case 0:
                    this.queuedPlayers.add(packet.getPlayer());
                    break;
                case 1:
                    if (packet.getTarget() == null) {
                        return;
                    }

                    this.queuedSpectators.put(packet.getPlayer(), packet.getTarget());
                    break;
                default:
                    Bukkit.getLogger().warning("Received an unknown packet queue type of " + packet.getType() + ". This packet will be ignored.");
            }
        } else if (object instanceof PacketPlayOutCreateMinigame) {
            PacketPlayOutCreateMinigame packet = (PacketPlayOutCreateMinigame) object;
            switch (packet.getMode()) {
                case 0:
                    this.minigame = new VanillaManhuntMinigame(packet.getGameKey(), this.plugin.getLobbyLocation());
                    break;
                case 1:
                    this.minigame = new DeathSwapMinigame(packet.getGameKey(), this.plugin.getLobbyLocation());
                    break;
                case 2:
                    this.minigame = new LockOutMinigame(packet.getGameKey(), this.plugin.getLobbyLocation());
                    break;
                default:
                    Bukkit.getLogger().warning("Received an unknown minigame creation request of ID " + packet.getMode() + ". This minigame request will be ignored...");
                    return;
            }

            Bukkit.getLogger().info(ChatColor.GREEN + "Reached max player statement");
            this.maxPlayers = packet.getMaxPlayers();
        } else if (object instanceof PacketPlayOutForceEnd) {
            Bukkit.getScheduler().runTask(this.plugin, this::handleForceEnd);
        }
    }

    public void handleForceEnd() {
        if (this.minigame == null) {
            return;
        }

        Bukkit.broadcast(Component.text(ChatColor.GREEN + "The " + this.minigame.getName() + " you were in was forcefully ended."));
        switch (this.minigame.getStatus()) {
            case IN_PROGRESS:
                this.minigame.endMinigame(true);
                break;
            case FINISHED:
                this.minigame.endTeleport();
                this.minigame.deleteWorlds();
                break;
            default:
                if (this.minigame.getStatus().isCountingDown()) {
                    this.queuedPlayers.addAll(this.minigame.getPlayers());
                    this.minigame.cancelCountdown();
                }

                if (this.queuedPlayers.isEmpty()) {
                    return;
                }

                try {
                    this.socket.sendPacket(new PacketPlayInEndMinigame(this.queuedPlayers));
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public void handleDisconnect(@NotNull Player player) {
        if (this.minigame == null) {
            return;
        }

        switch (this.minigame.getStatus()) {
            case WAITING:
                if (!this.queuedPlayers.remove(player.getUniqueId())) {
                    return;
                }

                if (this.queuedPlayers.isEmpty()) {
                    try {
                        this.socket.sendPacket(new PacketPlayInServerQueue(true));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        this.maxPlayers = 0;
                        this.minigame.deleteWorlds(true);
                        this.minigame.unload();
                        this.queuedSpectators.clear();
                        this.minigame = null;
                    }
                } else {
                    try {
                        this.socket.sendPacket(new PacketPlayInUpdatePlayerStatus(player.getUniqueId(), (byte) 0));
                        this.socket.sendPacket(new PacketPlayInUpdatePlayerCount(this.queuedPlayers.size()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                break;
            case COUNTING_DOWN:
                this.minigame.removePlayer(player);
                try {
                    this.socket.sendPacket(new PacketPlayInUpdatePlayerStatus(player.getUniqueId(), (byte) 0));
                    this.socket.sendPacket(new PacketPlayInUpdatePlayerCount(this.minigame.getNumPlayers()));
                    if (this.minigame.getNumPlayers() < this.maxPlayers) {
                        this.queuedPlayers.addAll(this.minigame.getPlayers());
                        this.minigame.cancelCountdown();
                        this.socket.sendPacket(new PacketPlayInServerQueue(this.queuedPlayers.isEmpty()));
                        if (this.queuedPlayers.isEmpty()) {
                            this.maxPlayers = 0;
                            this.minigame.deleteWorlds(true);
                            this.minigame.unload();
                            this.queuedSpectators.clear();
                            this.minigame = null;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            case IN_PROGRESS:
                if (!this.minigame.isPlayer(player.getUniqueId())) {
                    return;
                }

                if (this.minigame.isSpectator(player.getUniqueId())) {
                    this.minigame.removePlayer(player);
                } else {
                    Bukkit.broadcast(this.minigame.getGameDisplayName(player).append(Component.text(ChatColor.GRAY + " disconnected.")));
                    this.disconnections.put(player.getUniqueId(), Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                        this.disconnections.remove(player.getUniqueId());
                        try {
                            this.socket.sendPacket(new PacketPlayInPlayerDisconnect(player.getUniqueId(), (byte) 0));
                            this.socket.sendPacket(new PacketPlayInUpdatePlayerCount(this.minigame.getNumPlayers() - 1));
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            this.minigame.removePlayer(player);
                        }

                    }, TimeUnit.MINUTES.toSeconds(3) * 20));
                }

                break;
            default:
        }
    }

    public void handleConnect(@NotNull Player player) {
        if (player.isDead()) {
            player.spigot().respawn();
        }

        if (player.getGameMode() != GameMode.SURVIVAL) {
            player.setGameMode(GameMode.SURVIVAL);
        }

        if (this.minigame == null) {
            return;
        }

        switch (minigame.getStatus()) {
            case WAITING:
                if (this.queuedPlayers.remove(player.getUniqueId())) {
                    this.queuedPlayers.add(player.getUniqueId());
                    this.teleportToLobby(player);
                    player.getInventory().clear();
                    player.setFireTicks(0);
                    if (this.queuedPlayers.size() >= this.maxPlayers) {
                        if (this.minigame.createWorlds()) {
                            try {
                                this.socket.sendPacket(new PacketPlayInStartCountdown(this.minigame.getGameKey()));
                                this.minigame.startCountdown(new ArrayList<>(this.queuedPlayers));
                                this.queuedPlayers.clear();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                Bukkit.broadcast(Component.text(ChatColor.RED + "An error occurred while loading up the world for " + this.minigame.getName() + ". You have been sent back to the minigame lobby."));
                                this.socket.sendPacket(new PacketPlayInPlayerAction(this.queuedPlayers, (byte) 0));
                                this.socket.sendPacket(new PacketPlayInServerQueue(true));
                                this.minigame.deleteWorlds();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                this.queuedPlayers.clear();
                                this.queuedSpectators.clear();
                            }
                        }
                    }
                }

                break;
            case IN_PROGRESS:
            case FINISHED:
                this.queuedSpectators.computeIfPresent(player.getUniqueId(), (k, v) -> {
                    Player target = Bukkit.getPlayer(v);
                    if (target == null) {
                        this.sendToProxyLobby(player);
                        return null;
                    }

                    player.teleportAsync(target.getLocation()).thenAccept(result -> {
                        if (result) {
                            this.minigame.hideSpectator(player);
                            PlayerUtil.setSpectator(player);
                            player.sendMessage(ChatColor.GREEN + "You are now spectating " + target.getName() + ".");
                        } else {
                            player.sendMessage(ChatColor.RED + "Failed to teleport you to " + target.getName() + ". You have been connected back to the lobby.");
                            this.sendToProxyLobby(player);
                        }
                    });

                    return null;
                });

                this.disconnections.computeIfPresent(player.getUniqueId(), (uniqueId, task) -> {
                    task.cancel();
                    if (this.minigame.isPlayer(uniqueId) && !this.minigame.isSpectator(uniqueId)) {
                        Bukkit.broadcast(this.minigame.getGameDisplayName(player).append(Component.text(ChatColor.GRAY + " reconnected.")));
                    }

                    return null;
                });

                break;
            default:
        }
    }

    public void handleEvent(@NotNull Event event, @Nullable UUID uniqueId) {
        if (event instanceof MinigameEvent) {
            if (event instanceof MinigameStartEvent) {
                MinigameStartEvent e = (MinigameStartEvent) event;
                try {
                    this.socket.sendPacket(new PacketPlayInUpdatePlayerStatus(e.getMinigame().getPlayers(), (byte) 2));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                MinigameEndEvent e = (MinigameEndEvent) event;
                Bukkit.getLogger().info(ChatColor.RED + "Ending minigame for " + e.getMinigame().getName() + " with game key " + e.getMinigame().getGameKey() + ".");
                try {
                    this.socket.sendPacket(new PacketPlayInEndMinigame(e.getMinigame().getPlayers()));
                } catch (IOException ex) {
                    ex.printStackTrace();
                } finally {
                    Iterator<Map.Entry<UUID, BukkitTask>> iterator = this.disconnections.entrySet().iterator();
                    while (iterator.hasNext()) {
                        iterator.next().getValue().cancel();
                        iterator.remove();
                    }

                    this.maxPlayers = 0;
                    this.minigame.unload();
                    this.queuedSpectators.clear();
                    this.minigame = null;
                }
            }

            return;
        }

        if (event instanceof AsyncChatEvent) {
            AsyncChatEvent e = (AsyncChatEvent) event;
            if (this.minigame == null) {
                e.renderer(new LobbyChatRenderer());
                return;
            }

            Iterator<Audience> iterator = e.viewers().iterator();
            while (iterator.hasNext()) {
                Audience audience = iterator.next();
                if (!(audience instanceof Player)) {
                    iterator.remove();
                    continue;
                }

                Player recipient = (Player) audience;
                if (this.minigame.isPlayer(uniqueId) ? (this.minigame.isSpectator(uniqueId) ? !this.minigame.isSpectator(recipient.getUniqueId()) : !this.minigame.isPlayer(recipient.getUniqueId())) : this.minigame.isPlayer(recipient.getUniqueId())) {
                    iterator.remove();
                    continue;
                }

                e.renderer(new GameChatRenderer(this.minigame));
            }
        } else {
            if (this.minigame == null) {
                return;
            }

            if (this.minigame.isPlayer(uniqueId) && !this.minigame.isSpectator(uniqueId)) {
                this.minigame.handleEvent(event);
            } else {
                this.minigame.handleSpectatorEvent(event);
            }
        }
    }

    @Nullable
    public AbstractMinigame getMinigame() {
        return this.minigame;
    }
}
