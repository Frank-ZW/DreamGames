package net.craftgalaxy.lockout.challenge.impl.misc;

import net.craftgalaxy.lockout.challenge.types.AbstractPlayerDamageChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

public class ChallengeWitherDamage extends AbstractPlayerDamageChallenge {

    public ChallengeWitherDamage(LockOutMinigame lockOut) {
        super(lockOut);
    }

    @Override
    public boolean onPlayerDamage(EntityDamageEvent e) {
        if (e.getCause() == this.getDamageCause()) {
            this.lockOut.completeChallenge((Player) e.getEntity(), this);
            return true;
        }

        return false;
    }

    @Override
    public @NotNull String getDisplayMessage() {
        return "Get Withered!";
    }

    @Override
    public @NotNull EntityDamageEvent.DamageCause getDamageCause() {
        return EntityDamageEvent.DamageCause.WITHER;
    }
}
