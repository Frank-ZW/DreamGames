package net.craftgalaxy.minigameservice.packet.client;

import net.craftgalaxy.minigameservice.packet.MinigamePacketPlayOut;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.UUID;

public class PacketPlayOutQueuePlayer extends MinigamePacketPlayOut implements Serializable {

    private static final long serialVersionUID = 1053762691156843276L;
    private final UUID player;
    private final byte type;
    private final UUID target;

    public PacketPlayOutQueuePlayer(@NotNull UUID player, byte type, @Nullable UUID target) {
        this.player = player;
        this.type = type;
        this.target = target;
    }

    @NotNull
    public UUID getPlayer() {
        return this.player;
    }

    public byte getType() {
        return this.type;
    }

    @Nullable
    public UUID getTarget() {
        return this.target;
    }
}
