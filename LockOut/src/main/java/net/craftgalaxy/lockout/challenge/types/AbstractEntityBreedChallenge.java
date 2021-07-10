package net.craftgalaxy.lockout.challenge.types;

import net.craftgalaxy.lockout.challenge.AbstractChallenge;
import net.craftgalaxy.lockout.challenge.functional.IEntityChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityBreedEvent;

public abstract class AbstractEntityBreedChallenge extends AbstractChallenge implements IEntityChallenge {

    public AbstractEntityBreedChallenge(LockOutMinigame lockOut) {
        super(lockOut);
    }

    @Override
    public boolean handle(Object object) {
        if (object instanceof EntityBreedEvent) {
            EntityBreedEvent e = (EntityBreedEvent) object;
            if (e.getBreeder() instanceof Player && e.getEntityType() == this.getEntity()) {
                this.lockOut.completeChallenge((Player) e.getBreeder(), this);
                return true;
            }
        }

        return false;
    }
}
