package net.craftgalaxy.lockout.challenge.types;

import net.craftgalaxy.lockout.challenge.AbstractChallenge;
import net.craftgalaxy.lockout.challenge.functional.IStructureChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractStructureChallenge extends AbstractChallenge implements IStructureChallenge {

    public AbstractStructureChallenge(LockOutMinigame lockOut) {
        super(lockOut);
    }

    @Override
    public boolean handle(Object object) {
        if (object instanceof Player) {
            Player player = (Player) object;
            if (this.isInsideStructure(player)) {
                this.lockOut.completeChallenge(player, this);
                return true;
            }
        }

        return false;
    }

    private boolean isInsideStructure(@NotNull Player player) {
        Location location = player.getWorld().locateNearestStructure(player.getLocation(), this.getStructureType(), 1, false);
        if (location == null) {
            return false;
        }

        World world = ((CraftWorld) player.getWorld()).getHandle();
        Chunk chunk = world.getChunkAt(location.getChunk().getX(), location.getChunk().getZ());
        StructureStart<?> start = chunk.a(this.getGenerator());
        if (start != null) {
            for (StructurePiece piece : start.d()) {
                StructureBoundingBox cuboid = piece.g();
                if (player.getLocation().getX() >= cuboid.a && player.getLocation().getX() <= cuboid.d && player.getLocation().getY() >= cuboid.b && player.getLocation().getY() <= cuboid.e && player.getLocation().getZ() >= cuboid.c && player.getLocation().getZ() <= cuboid.f) {
                    return true;
                }
            }
        }

        return false;
    }
}
