package net.craftgalaxy.lockout.challenge.impl.entity;

import net.craftgalaxy.lockout.challenge.AbstractChallenge;
import net.craftgalaxy.lockout.challenge.functional.IEntityChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTameEvent;
import org.jetbrains.annotations.NotNull;

public class ChallengeTameCat extends AbstractChallenge implements IEntityChallenge {

    public ChallengeTameCat(LockOutMinigame lockOut) {
        super(lockOut);
    }

    @Override
    public @NotNull String getDisplayMessage() {
        return "Tame a cat!";
    }

    @Override
    public boolean handle(Object object) {
        if (object instanceof EntityTameEvent) {
            EntityTameEvent e = (EntityTameEvent) object;
            if (e.getEntity().getType() == this.getEntity() && e.getOwner() instanceof Player) {
                this.lockOut.completeChallenge((Player) e.getOwner(), this);
                return true;
            }
        }

        return false;
    }

    @Override
    public @NotNull EntityType getEntity() {
        return EntityType.CAT;
    }
}
