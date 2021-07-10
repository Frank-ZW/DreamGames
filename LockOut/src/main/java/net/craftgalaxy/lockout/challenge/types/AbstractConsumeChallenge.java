package net.craftgalaxy.lockout.challenge.types;

import net.craftgalaxy.lockout.challenge.AbstractChallenge;
import net.craftgalaxy.lockout.challenge.functional.IItemChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public abstract class AbstractConsumeChallenge extends AbstractChallenge implements IItemChallenge {

    public AbstractConsumeChallenge(LockOutMinigame lockOut) {
        super(lockOut);
    }

    @Override
    public boolean handle(Object object) {
        if (object instanceof PlayerItemConsumeEvent) {
            PlayerItemConsumeEvent e = (PlayerItemConsumeEvent) object;
            if (e.getItem().getType() == this.getItemType()) {
                this.lockOut.completeChallenge(e.getPlayer(), this);
                return true;
            }
        }

        return false;
    }
}
