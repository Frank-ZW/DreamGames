package net.craftgalaxy.lockout.challenge.impl.movement;

import net.craftgalaxy.lockout.challenge.types.AbstractMovementChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

public class ChallengeReachBedrock extends AbstractMovementChallenge {

    public ChallengeReachBedrock(LockOutMinigame lockOut) {
        super(lockOut);
    }

    @Override
    public @NotNull String getDisplayMessage() {
        return "Reach bedrock level!";
    }

    @Override
    public boolean onPlayerMove(PlayerMoveEvent e) {
        if (e.getTo().getBlockY() < 6) {
            Block block = e.getTo().clone().subtract(0.0D, 1.0D, 0.0D).getBlock();
            if (block.getType() == Material.BEDROCK) {
                this.lockOut.completeChallenge(e.getPlayer(), this);
                return true;
            }
        }

        return false;
    }
}
