package net.craftgalaxy.minigameservice.packet.server;

import net.craftgalaxy.minigameservice.packet.MinigamePacketPlayIn;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class PacketPlayInPlayerAction extends MinigamePacketPlayIn implements Serializable {

    private static final long serialVersionUID = -3153239630303535943L;
    private final byte action;
    private final Collection<UUID> players;

    public PacketPlayInPlayerAction(byte action, UUID player) {
        this(action, Collections.singletonList(player));
    }

    public PacketPlayInPlayerAction(byte action, Collection<UUID> players) {
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
