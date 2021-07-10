package net.craftgalaxy.lockout.challenge.impl.misc;

import net.craftgalaxy.lockout.challenge.AbstractChallenge;
import net.craftgalaxy.lockout.challenge.functional.IMultiEntityChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerFishEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ChallengeCatchFish extends AbstractChallenge implements IMultiEntityChallenge {

    private final Set<EntityType> fish = Set.of(EntityType.TROPICAL_FISH, EntityType.PUFFERFISH, EntityType.SALMON);

    public ChallengeCatchFish(LockOutMinigame lockOut) {
        super(lockOut);
    }

    @Override
    public @NotNull String getDisplayMessage() {
        return "Catch a fish!";
    }

    @Override
    public boolean handle(Object object) {
        if (object instanceof PlayerFishEvent) {
            PlayerFishEvent e = (PlayerFishEvent) object;
            if (e.getCaught() != null && this.getEntities().contains(e.getCaught().getType())) {
                this.lockOut.completeChallenge(e.getPlayer(), this);
                return true;
            }
        }

        return false;
    }

    @Override
    public @NotNull Set<EntityType> getEntities() {
        return this.fish;
    }
}
