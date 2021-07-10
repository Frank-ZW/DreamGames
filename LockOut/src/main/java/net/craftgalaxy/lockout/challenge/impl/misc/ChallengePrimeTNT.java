package net.craftgalaxy.lockout.challenge.impl.misc;

import com.destroystokyo.paper.event.block.TNTPrimeEvent;
import net.craftgalaxy.lockout.challenge.AbstractChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ChallengePrimeTNT extends AbstractChallenge {

    public ChallengePrimeTNT(LockOutMinigame lockOut) {
        super(lockOut);
    }

    @Override
    public @NotNull String getDisplayMessage() {
        return "Detonate a block of TNT!";
    }

    @Override
    public boolean handle(Object object) {
        if (object instanceof TNTPrimeEvent) {
            TNTPrimeEvent e = (TNTPrimeEvent) object;
            if (e.getPrimerEntity() instanceof Player) {
                this.lockOut.completeChallenge((Player) e.getPrimerEntity(), this);
                return true;
            }
        }

        return false;
    }
}
