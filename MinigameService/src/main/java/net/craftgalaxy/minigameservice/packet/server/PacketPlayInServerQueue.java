package net.craftgalaxy.minigameservice.packet.server;

import net.craftgalaxy.minigameservice.packet.MinigamePacketPlayIn;

import java.io.Serializable;

public class PacketPlayInServerQueue extends MinigamePacketPlayIn implements Serializable {

    private static final long serialVersionUID = 7890078131915230689L;
    private final boolean reset;

    public PacketPlayInServerQueue(boolean reset) {
        this.reset = reset;
    }

    public boolean isReset() {
        return this.reset;
    }
}
