package net.craftgalaxy.minigameservice.packet.server;

import net.craftgalaxy.minigameservice.packet.MinigamePacketPlayIn;

import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;

public class PacketPlayInEndMinigame extends MinigamePacketPlayIn implements Serializable {

    private static final long serialVersionUID = 3306685372671824034L;
    private final Collection<UUID> players;

    public PacketPlayInEndMinigame(Collection<UUID> players) {
        this.players = players;
    }

    public Collection<UUID> getPlayers() {
        return this.players;
    }
}
