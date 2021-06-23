package net.craftgalaxy.minigameservice.packet.server;

import net.craftgalaxy.minigameservice.packet.MinigamePacketPlayIn;

import java.io.Serializable;

public class PacketPlayInUpdatePlayerCount extends MinigamePacketPlayIn implements Serializable {

    private static final long serialVersionUID = 8220077767590097080L;
    private final int players;

    public PacketPlayInUpdatePlayerCount(int players) {
        this.players = players;
    }

    public int getPlayers() {
        return this.players;
    }
}
