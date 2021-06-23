package net.craftgalaxy.minigameservice.packet.server;

import net.craftgalaxy.minigameservice.packet.MinigamePacketPlayIn;

import java.io.Serializable;
import java.util.UUID;

public class PacketPlayInPlayerDisconnect extends MinigamePacketPlayIn implements Serializable {

    private static final long serialVersionUID = -6799620506577480235L;
    private final UUID player;
    private final byte action;

    public PacketPlayInPlayerDisconnect(UUID player, byte action) {
        this.player = player;
        this.action = action;
    }

    public UUID getPlayer() {
        return this.player;
    }

    public byte getAction() {
        return this.action;
    }
}
