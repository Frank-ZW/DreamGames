package net.craftgalaxy.minigameservice.packet.client;

import net.craftgalaxy.minigameservice.packet.MinigamePacketPlayOut;

import java.io.Serializable;

public class PacketPlayOutPromptDisconnect extends MinigamePacketPlayOut implements Serializable {

    private static final long serialVersionUID = -8957843648406322185L;
    private boolean shutdown;

    public PacketPlayOutPromptDisconnect(boolean shutdown) {
        this.shutdown = shutdown;
    }

    public boolean isShutdown() {
        return this.shutdown;
    }
}
