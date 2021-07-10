package net.craftgalaxy.lockout.challenge.types;

import net.craftgalaxy.lockout.challenge.AbstractChallenge;
import net.craftgalaxy.lockout.challenge.functional.IDamageChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public abstract class AbstractPlayerDamageChallenge extends AbstractChallenge implements IDamageChallenge {

    public AbstractPlayerDamageChallenge(LockOutMinigame lockOut) {
        super(lockOut);
    }

    @Override
    public boolean handle(Object object) {
        if (object instanceof EntityDamageEvent) {
            EntityDamageEvent e = (EntityDamageEvent) object;
            if (e.getEntity() instanceof Player) {
                return this.onPlayerDamage(e);
            }
        }

        return false;
    }

    public abstract boolean onPlayerDamage(EntityDamageEvent e);
}
