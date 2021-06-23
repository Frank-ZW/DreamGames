package net.craftgalaxy.minigameservice.packet.server;

import net.craftgalaxy.minigameservice.packet.MinigamePacketPlayIn;

import java.io.Serializable;

public class PacketPlayInStartCountdown extends MinigamePacketPlayIn implements Serializable {

    private static final long serialVersionUID = -7248998589837458110L;
    private final int gameKey;

    public PacketPlayInStartCountdown(int gameKey) {
        this.gameKey = gameKey;
    }

    public int getGameKey() {
        return this.gameKey;
    }
}
