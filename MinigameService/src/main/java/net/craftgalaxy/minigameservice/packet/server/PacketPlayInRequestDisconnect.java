package net.craftgalaxy.minigameservice.packet.server;

import net.craftgalaxy.minigameservice.packet.MinigamePacketPlayIn;

import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;

public class PacketPlayInRequestDisconnect extends MinigamePacketPlayIn implements Serializable {

    private static final long serialVersionUID = -2414154426484112157L;
    private final Collection<UUID> players;

    public PacketPlayInRequestDisconnect(Collection<UUID> players) {
        this.players = players;
    }

    public Collection<UUID> getPlayers() {
        return this.players;
    }
}
