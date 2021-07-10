package net.craftgalaxy.lockout.challenge.impl.entity;

import net.craftgalaxy.lockout.challenge.AbstractChallenge;
import net.craftgalaxy.lockout.challenge.functional.IEntityChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.jetbrains.annotations.NotNull;

public class ChallengeHatchChicken extends AbstractChallenge implements IEntityChallenge {

    public ChallengeHatchChicken(LockOutMinigame lockOut) {
        super(lockOut);
    }

    @Override
    public @NotNull String getDisplayMessage() {
        return "Hatch a chicken from an egg!";
    }

    @Override
    public boolean handle(Object object) {
        if (object instanceof PlayerEggThrowEvent) {
            PlayerEggThrowEvent e = (PlayerEggThrowEvent) object;
            if (e.isHatching() && e.getHatchingType() == this.getEntity()) {
                this.lockOut.completeChallenge(e.getPlayer(), this);
                return true;
            }
        }

        return false;
    }

    @Override
    public @NotNull EntityType getEntity() {
        return EntityType.CHICKEN;
    }
}
