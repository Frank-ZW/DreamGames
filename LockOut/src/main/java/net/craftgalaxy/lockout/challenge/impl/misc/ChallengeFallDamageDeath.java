package net.craftgalaxy.lockout.challenge.impl.misc;

import net.craftgalaxy.lockout.challenge.types.AbstractPlayerDamageChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

public class ChallengeFallDamageDeath extends AbstractPlayerDamageChallenge {

    public ChallengeFallDamageDeath(LockOutMinigame lockOut) {
        super(lockOut);
    }

    @Override
    public @NotNull String getDisplayMessage() {
        return "Die from fall damage!";
    }

    @Override
    public boolean onPlayerDamage(EntityDamageEvent e) {
        Player player = (Player) e.getEntity();
        if (player.getHealth() - e.getDamage() <= 0 && e.getCause() == this.getDamageCause()) {
            this.lockOut.completeChallenge(player, this);
            return true;
        }

        return false;
    }

    @Override
    public @NotNull EntityDamageEvent.DamageCause getDamageCause() {
        return EntityDamageEvent.DamageCause.FALL;
    }
}
