package net.craftgalaxy.lockout.challenge.impl.movement;

import net.craftgalaxy.lockout.challenge.types.AbstractMovementChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

public class ChallengeReachHeightLimit extends AbstractMovementChallenge {

    public ChallengeReachHeightLimit(LockOutMinigame lockOut) {
        super(lockOut);
    }

    @Override
    public @NotNull String getDisplayMessage() {
        return "Reach the height limit!";
    }

    @Override
    public boolean onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (e.getTo().getBlockY() >= player.getWorld().getMaxHeight()) {
            this.lockOut.completeChallenge(player, this);
            return true;
        }

        return false;
    }
}
