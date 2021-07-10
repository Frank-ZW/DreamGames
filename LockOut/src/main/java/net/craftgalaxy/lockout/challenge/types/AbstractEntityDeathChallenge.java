package net.craftgalaxy.lockout.challenge.types;

import net.craftgalaxy.lockout.challenge.AbstractChallenge;
import net.craftgalaxy.lockout.challenge.functional.IEntityChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;

public abstract class AbstractEntityDeathChallenge extends AbstractChallenge implements IEntityChallenge {

    public AbstractEntityDeathChallenge(LockOutMinigame lockOut) {
        super(lockOut);
    }

    @Override
    public boolean handle(Object object) {
        if (object instanceof EntityDeathEvent) {
            EntityDeathEvent e = (EntityDeathEvent) object;
            if (e.getEntity().getType() == this.getEntity()) {
                Player killer = e.getEntity().getKiller();
                if (killer != null) {
                    this.lockOut.completeChallenge(killer, this);
                    return true;
                }
            }
        }

        return false;
    }
}
