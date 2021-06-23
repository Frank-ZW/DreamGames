package net.craftgalaxy.bungeecore.server;

import net.craftgalaxy.bungeecore.player.PlayerData;
import net.craftgalaxy.bungeecore.player.manager.PlayerManager;
import net.craftgalaxy.bungeecore.server.manager.ServerManager;
import net.craftgalaxy.bungeecore.BungeeCore;
import net.craftgalaxy.minigameservice.packet.MinigamePacketPlayOut;
import net.craftgalaxy.minigameservice.packet.client.PacketPlayOutQueuePlayer;
import net.craftgalaxy.minigameservice.packet.server.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

public class ServerData implements Runnable {

    private final BungeeCore plugin;
    private final Socket socket;
    private final int serverId;
    private final Set<UUID> pendingDisconnections = new HashSet<>();
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServerInfo server;
    private Minigames minigame;
    private ServerType serverType;
    private boolean requestedDisconnect;
    private int maxPlayers;
    private int players;

    public ServerData(BungeeCore plugin, Socket socket, int serverId) {
        this.plugin = plugin;
        this.socket = socket;
        this.serverId = serverId;
        this.players = 0;
        this.minigame = Minigames.INACTIVE;
        this.serverType = ServerType.INACTIVE;
    }

    public String getServerName() {
        return this.server == null ? "Unknown" : this.server.getName();
    }

    public int getServerId() {
        return this.serverId;
    }

    public ServerInfo getServer() {
        return this.server;
    }

    public Minigames getMinigame() {
        return this.minigame;
    }

    public void setMinigame(Minigames minigame) {
        this.minigame = minigame;
    }

    public ServerType getServerType() {
        return this.serverType;
    }

    public void setServerType(ServerType serverType) {
        this.serverType = serverType;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int incrementPlayers() {
        return ++this.players;
    }

    public int getPlayers() {
        return this.players;
    }

    public void setPlayers(int players) {
        this.players = players;
    }

    public void sendQueuedPlayer(@NotNull ProxiedPlayer player) {
        if (this.server == null) {
            player.sendMessage(new TextComponent(ChatColor.RED + "Failed to send you to the specified server. Contact an administrator if this occurs."));
            return;
        }

        try {
            this.sendPacket(new PacketPlayOutQueuePlayer(player.getUniqueId()));
            player.connect(this.server);
        } catch (IOException e) {
            player.sendMessage(new TextComponent(ChatColor.RED + "An error occurred while connecting you to " + this.server.getName() + "."));
        }
    }

    public ServerData reset() {
        this.minigame = Minigames.INACTIVE;
        this.maxPlayers = 0;
        this.players = 0;
        return this;
    }

    public void sendPacket(@NotNull MinigamePacketPlayOut packet) throws IOException {
        this.output.writeObject(packet);
        this.output.flush();
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        try {
            this.output = new ObjectOutputStream(this.socket.getOutputStream());
            this.input = new ObjectInputStream(this.socket.getInputStream());
            while (true) {
                Object object = this.input.readObject();
                if (object instanceof PacketPlayInServerConnect) {
                    PacketPlayInServerConnect packet = (PacketPlayInServerConnect) object;
                    this.server = this.plugin.getProxy().getServerInfo(packet.getName());
                    if (this.server == null) {
                        this.plugin.getLogger().warning("A server with the name " + packet.getName() + " has not been registered in the proxy.");
                    } else {
                        ServerManager.getInstance().connectServer(packet.getName(), this);
                    }
                } else if (object instanceof PacketPlayInRequestDisconnect) {
                    PacketPlayInRequestDisconnect packet = (PacketPlayInRequestDisconnect) object;
                    this.requestedDisconnect = true;
                    this.pendingDisconnections.addAll(packet.getPlayers());
                    if (this.pendingDisconnections.isEmpty()) {
                        ServerManager.getInstance().disconnectServer(this);
                        continue;
                    }

                    ServerInfo server = ServerManager.getInstance().getRandomLobby().getServer();
                    for (UUID uniqueId : packet.getPlayers()) {
                        PlayerData playerData = PlayerManager.getInstance().getPlayerData(uniqueId);
                        if (playerData != null) {
                            playerData.setPlayerStatus(PlayerData.PlayerStatus.INACTIVE);
                            playerData.getPlayer().connect(server);
                            playerData.getPlayer().sendMessage(new TextComponent(ChatColor.GREEN + "The server you were on has unexpectedly shutdown. You have been connected to the lobby."));
                        }
                    }
                } else if (object instanceof PacketPlayInStartCountdown) {
                    ServerManager.getInstance().addActiveServer(this, ((PacketPlayInStartCountdown) object).getGameKey());
                } else if (object instanceof PacketPlayInPlayerAction) {
                    PacketPlayInPlayerAction packet = (PacketPlayInPlayerAction) object;
                    switch (packet.getAction()) {
                        case 0:
                            ServerInfo server = ServerManager.getInstance().getRandomLobby().getServer();
                            for (UUID uniqueId : packet.getPlayers()) {
                                PlayerData playerData = PlayerManager.getInstance().getPlayerData(uniqueId);
                                if (playerData != null) {
                                    playerData.setPlayerStatus(PlayerData.PlayerStatus.INACTIVE);
                                    playerData.getPlayer().connect(server);
                                }
                            }

                            break;
                        case 1:
                            break;
                        default:
                            this.plugin.getLogger().warning("Received unknown player action of " + packet.getAction() + ". This action will be ignored.");
                    }
                } else if (object instanceof PacketPlayInServerQueue) {
                    ServerManager.getInstance().queueServer(this, ((PacketPlayInServerQueue) object).isReset());
                } else if (object instanceof PacketPlayInPlayerDisconnect) {
                    PacketPlayInPlayerDisconnect packet = (PacketPlayInPlayerDisconnect) object;
                    switch (packet.getAction()) {
                        case 0:
                            PlayerManager.getInstance().removeDisconnection(packet.getPlayer());
                            break;
                        case 1:
                            break;
                        default:
                    }
                } else if (object instanceof PacketPlayInUpdatePlayerStatus) {
                    PacketPlayInUpdatePlayerStatus packet = (PacketPlayInUpdatePlayerStatus) object;
                    if (packet.getStatus() < PlayerData.PlayerStatus.values().length) {
                        for (UUID uniqueId : packet.getPlayers()) {
                            PlayerData playerData = PlayerManager.getInstance().getPlayerData(uniqueId);
                            if (playerData != null) {
                                playerData.setPlayerStatus(PlayerData.PlayerStatus.values()[packet.getStatus()]);
                            }
                        }
                    }
                } else if (object instanceof PacketPlayInEndMinigame) {
                    PacketPlayInEndMinigame packet = (PacketPlayInEndMinigame) object;
                    ServerInfo server = ServerManager.getInstance().getRandomLobby().getServer();
                    for (UUID uniqueId : packet.getPlayers()) {
                        PlayerData playerData = PlayerManager.getInstance().getPlayerData(uniqueId);
                        if (playerData != null) {
                            playerData.setPlayerStatus(PlayerData.PlayerStatus.INACTIVE);
                            playerData.getPlayer().connect(server);
                        }
                    }

                    PlayerManager.getInstance().removeDisconnections(packet.getPlayers());
                    ServerManager.getInstance().addInactiveServer(this);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            if (!(e instanceof EOFException)) {
                e.printStackTrace();
            }
        } finally {
            try {
                this.output.close();
                this.input.close();
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void checkShouldDisconnect(ProxiedPlayer player) {
        if (this.requestedDisconnect && this.pendingDisconnections.remove(player.getUniqueId()) && this.pendingDisconnections.isEmpty()) {
            ServerManager.getInstance().disconnectServer(this);
        }
    }

    public enum Minigames {
        MANHUNT("Manhunt"),
        INACTIVE("Unknown");

        private final String displayName;

        Minigames(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return this.displayName;
        }
    }

    public enum ServerType {
        HUB,
        LOBBY,
        MINIGAME,
        INACTIVE;

        ServerType() {}
    }

    /**
     * Returns a hash code value for the object. This method is
     * supported for the benefit of hash tables such as those provided by
     * {@link HashMap}.
     * <p>
     * The general contract of {@code hashCode} is:
     * <ul>
     * <li>Whenever it is invoked on the same object more than once during
     *     an execution of a Java application, the {@code hashCode} method
     *     must consistently return the same integer, provided no information
     *     used in {@code equals} comparisons on the object is modified.
     *     This integer need not remain consistent from one execution of an
     *     application to another execution of the same application.
     * <li>If two objects are equal according to the {@code equals(Object)}
     *     method, then calling the {@code hashCode} method on each of
     *     the two objects must produce the same integer result.
     * <li>It is <em>not</em> required that if two objects are unequal
     *     according to the {@link Object#equals(Object)}
     *     method, then calling the {@code hashCode} method on each of the
     *     two objects must produce distinct integer results.  However, the
     *     programmer should be aware that producing distinct integer results
     *     for unequal objects may improve the performance of hash tables.
     * </ul>
     * <p>
     * As much as is reasonably practical, the hashCode method defined
     * by class {@code Object} does return distinct integers for
     * distinct objects. (The hashCode may or may not be implemented
     * as some function of an object's memory address at some point
     * in time.)
     *
     * @return a hash code value for this object.
     * @see Object#equals(Object)
     * @see System#identityHashCode
     */
    @Override
    public int hashCode() {
        return 43 * this.serverId;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * <p>
     * The {@code equals} method implements an equivalence relation
     * on non-null object references:
     * <ul>
     * <li>It is <i>reflexive</i>: for any non-null reference value
     *     {@code x}, {@code x.equals(x)} should return
     *     {@code true}.
     * <li>It is <i>symmetric</i>: for any non-null reference values
     *     {@code x} and {@code y}, {@code x.equals(y)}
     *     should return {@code true} if and only if
     *     {@code y.equals(x)} returns {@code true}.
     * <li>It is <i>transitive</i>: for any non-null reference values
     *     {@code x}, {@code y}, and {@code z}, if
     *     {@code x.equals(y)} returns {@code true} and
     *     {@code y.equals(z)} returns {@code true}, then
     *     {@code x.equals(z)} should return {@code true}.
     * <li>It is <i>consistent</i>: for any non-null reference values
     *     {@code x} and {@code y}, multiple invocations of
     *     {@code x.equals(y)} consistently return {@code true}
     *     or consistently return {@code false}, provided no
     *     information used in {@code equals} comparisons on the
     *     objects is modified.
     * <li>For any non-null reference value {@code x},
     *     {@code x.equals(null)} should return {@code false}.
     * </ul>
     * <p>
     * The {@code equals} method for class {@code Object} implements
     * the most discriminating possible equivalence relation on objects;
     * that is, for any non-null reference values {@code x} and
     * {@code y}, this method returns {@code true} if and only
     * if {@code x} and {@code y} refer to the same object
     * ({@code x == y} has the value {@code true}).
     * <p>
     * Note that it is generally necessary to override the {@code hashCode}
     * method whenever this method is overridden, so as to maintain the
     * general contract for the {@code hashCode} method, which states
     * that equal objects must have equal hash codes.
     *
     * @param obj the reference object with which to compare.
     * @return {@code true} if this object is the same as the obj
     * argument; {@code false} otherwise.
     * @see #hashCode()
     * @see HashMap
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof ServerData)) {
            return false;
        }

        ServerData o = (ServerData) obj;
        return this.serverId == o.getServerId();
    }
}
