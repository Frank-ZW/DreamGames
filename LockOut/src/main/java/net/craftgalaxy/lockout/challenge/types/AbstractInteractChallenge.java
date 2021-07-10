package net.craftgalaxy.lockout.challenge.types;

import net.craftgalaxy.lockout.challenge.AbstractChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import org.bukkit.event.player.PlayerInteractEvent;

public abstract class AbstractInteractChallenge extends AbstractChallenge {

    public AbstractInteractChallenge(LockOutMinigame lockOut) {
        super(lockOut);
    }

    @Override
    public boolean handle(Object object) {
        if (object instanceof PlayerInteractEvent) {
            return this.onPlayerInteract((PlayerInteractEvent) object);
        }

        return false;
    }

    public abstract boolean onPlayerInteract(PlayerInteractEvent e);
}
