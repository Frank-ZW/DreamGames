package net.craftgalaxy.minigameservice.packet.client;

import net.craftgalaxy.minigameservice.packet.MinigamePacketPlayOut;

import java.io.Serializable;
import java.util.UUID;

public class PacketPlayOutQueuePlayer extends MinigamePacketPlayOut implements Serializable {

    private static final long serialVersionUID = 1053762691156843276L;
    private final UUID player;

    public PacketPlayOutQueuePlayer(UUID player) {
        this.player = player;
    }

    public UUID getPlayer() {
        return this.player;
    }
}
