package net.craftgalaxy.lockout.challenge.types;

import net.craftgalaxy.lockout.challenge.AbstractChallenge;
import net.craftgalaxy.lockout.challenge.functional.IBiomeChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import org.bukkit.event.player.PlayerMoveEvent;

public abstract class AbstractMovementChallenge extends AbstractChallenge {

    public AbstractMovementChallenge(LockOutMinigame lockOut) {
        super(lockOut);
    }

    @Override
    public boolean handle(Object object) {
        if (object instanceof PlayerMoveEvent) {
            PlayerMoveEvent e = (PlayerMoveEvent) object;
            return this.onPlayerMove(e);
        }

        return false;
    }

    public abstract boolean onPlayerMove(PlayerMoveEvent e);
}
