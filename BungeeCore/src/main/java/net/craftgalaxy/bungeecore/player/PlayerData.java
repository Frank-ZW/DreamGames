package net.craftgalaxy.bungeecore.player;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class PlayerData {

    private final ProxiedPlayer player;
    private final UUID uniqueId;
    private final String name;
    private PlayerStatus playerStatus;

    public PlayerData(ProxiedPlayer player) {
        this.player = player;
        this.uniqueId = player.getUniqueId();
        this.name = player.getName();
        this.playerStatus = PlayerStatus.INACTIVE;
    }

    public ProxiedPlayer getPlayer() {
        return this.player;
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public String getName() {
        return this.name;
    }

    public PlayerStatus getPlayerStatus() {
        return this.playerStatus;
    }

    public void setPlayerStatus(PlayerStatus playerStatus) {
        this.playerStatus = playerStatus;
    }

    public boolean isInactive() {
        return this.playerStatus == PlayerStatus.INACTIVE;
    }

    public boolean isPlaying() {
        return this.playerStatus == PlayerStatus.PLAYING;
    }

    public enum PlayerStatus {
        INACTIVE,
        QUEUING,
        PLAYING,
        SPECTATING;

        PlayerStatus() {}
    }
}
