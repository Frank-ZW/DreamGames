package net.craftgalaxy.minigameservice.packet.server;

import net.craftgalaxy.minigameservice.packet.MinigamePacketPlayIn;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class PacketPlayInUpdatePlayerStatus extends MinigamePacketPlayIn implements Serializable {

    private static final long serialVersionUID = 9125239549474129592L;
    private final Collection<UUID> players;
    private final byte status;

    public PacketPlayInUpdatePlayerStatus(UUID player, byte status) {
        this(Collections.singletonList(player), status);
    }

    public PacketPlayInUpdatePlayerStatus(Collection<UUID> players, byte status) {
        this.players = players;
        this.status = status;
    }

    public Collection<UUID> getPlayers() {
        return this.players;
    }

    public byte getStatus() {
        return this.status;
    }
}
