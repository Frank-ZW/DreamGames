package net.craftgalaxy.lockout.challenge.impl.entity;

import net.craftgalaxy.lockout.challenge.types.AbstractEntityBreedChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public class ChallengeBreedHorses extends AbstractEntityBreedChallenge {

    public ChallengeBreedHorses(LockOutMinigame lockOut) {
        super(lockOut);
    }

    @Override
    public @NotNull String getDisplayMessage() {
        return "Breed two horses!";
    }

    @Override
    public @NotNull EntityType getEntity() {
        return EntityType.HORSE;
    }
}
