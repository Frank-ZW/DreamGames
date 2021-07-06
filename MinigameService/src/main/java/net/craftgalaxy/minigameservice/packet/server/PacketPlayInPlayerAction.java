package net.craftgalaxy.minigameservice.packet.server;

import net.craftgalaxy.minigameservice.packet.MinigamePacketPlayIn;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class PacketPlayInPlayerAction extends MinigamePacketPlayIn implements Serializable {

    private static final long serialVersionUID = -3153239630303535943L;
    private final Collection<UUID> players;
    private final byte action;

    public PacketPlayInPlayerAction(UUID player, byte action) {
        this(Collections.singletonList(player), action);
    }

    public PacketPlayInPlayerAction(Collection<UUID> players, byte action) {
        this.action = action;
        this.players = players;
    }

    public byte getAction() {
        return this.action;
    }

    public Collection<UUID> getPlayers() {
        return this.players;
    }
}
