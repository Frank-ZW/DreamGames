package net.craftgalaxy.lockout.challenge.impl.entity;

import net.craftgalaxy.lockout.challenge.AbstractChallenge;
import net.craftgalaxy.lockout.challenge.functional.IEntityChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.jetbrains.annotations.NotNull;

public class ChallengeShearSheep extends AbstractChallenge implements IEntityChallenge {

    public ChallengeShearSheep(LockOutMinigame lockOut) {
        super(lockOut);
    }

    @Override
    public @NotNull String getDisplayMessage() {
        return "Shear a sheep!";
    }

    @Override
    public boolean handle(Object object) {
        if (object instanceof PlayerShearEntityEvent) {
            PlayerShearEntityEvent e = (PlayerShearEntityEvent) object;
            if (e.getEntity().getType() == this.getEntity()) {
                this.lockOut.completeChallenge(e.getPlayer(), this);
                return true;
            }
        }

        return false;
    }

    @Override
    public @NotNull EntityType getEntity() {
        return EntityType.SHEEP;
    }
}
