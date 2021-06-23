package net.craftgalaxy.minigameservice.packet.server;

import net.craftgalaxy.minigameservice.packet.MinigamePacketPlayIn;

import java.io.Serializable;

public class PacketPlayInServerConnect extends MinigamePacketPlayIn implements Serializable {

    private static final long serialVersionUID = -6619849865542755314L;
    private final String name;

    public PacketPlayInServerConnect(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
