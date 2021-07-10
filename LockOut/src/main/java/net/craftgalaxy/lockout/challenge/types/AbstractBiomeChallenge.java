package net.craftgalaxy.lockout.challenge.types;

import net.craftgalaxy.lockout.challenge.functional.IBiomeChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import org.bukkit.event.player.PlayerMoveEvent;

public abstract class AbstractBiomeChallenge extends AbstractMovementChallenge implements IBiomeChallenge {

    public AbstractBiomeChallenge(LockOutMinigame lockOut) {
        super(lockOut);
    }

    @Override
    public boolean onPlayerMove(PlayerMoveEvent e) {
        if (e.getTo().getBlock().getBiome() == this.getBiome()) {
            this.lockOut.completeChallenge(e.getPlayer(), this);
            return true;
        }

        return false;
    }
}
