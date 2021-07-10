package net.craftgalaxy.lockout.challenge.types;

import net.craftgalaxy.lockout.challenge.AbstractChallenge;
import net.craftgalaxy.lockout.challenge.functional.IAdvancementChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

public abstract class AbstractAdvancementChallenge extends AbstractChallenge implements IAdvancementChallenge {

    public AbstractAdvancementChallenge(LockOutMinigame lockOut) {
        super(lockOut);
    }

    @Override
    public boolean handle(Object object) {
        if (object instanceof PlayerAdvancementDoneEvent) {
            PlayerAdvancementDoneEvent e = (PlayerAdvancementDoneEvent) object;
            if (e.getAdvancement().getKey().getKey().equals(this.getAdvancementKey())) {
                this.lockOut.completeChallenge(e.getPlayer(), this);
                return true;
            }
        }

        return false;
    }
}
