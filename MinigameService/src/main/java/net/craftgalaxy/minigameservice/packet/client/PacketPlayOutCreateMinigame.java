package net.craftgalaxy.minigameservice.packet.client;

import net.craftgalaxy.minigameservice.packet.MinigamePacketPlayOut;

import java.io.Serializable;

public class PacketPlayOutCreateMinigame extends MinigamePacketPlayOut implements Serializable {

    private static final long serialVersionUID = -8683526233406867400L;
    private final int mode;
    private final int gameKey;
    private final int maxPlayers;

    public PacketPlayOutCreateMinigame(int mode, int gameKey, int maxPlayers) {
        this.mode = mode;
        this.gameKey = gameKey;
        this.maxPlayers = maxPlayers;
    }

    public int getMode() {
        return this.mode;
    }

    public int getGameKey() {
        return this.gameKey;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }
}
