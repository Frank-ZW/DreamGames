package net.craftgalaxy.lockout.challenge.impl.entity;

import net.craftgalaxy.lockout.challenge.types.AbstractEntityBreedChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public class ChallengeBreedChickens extends AbstractEntityBreedChallenge {

    public ChallengeBreedChickens(LockOutMinigame lockOut) {
        super(lockOut);
    }

    @Override
    public @NotNull String getDisplayMessage() {
        return "Breed two chickens!";
    }

    @Override
    public @NotNull EntityType getEntity() {
        return EntityType.CHICKEN;
    }
}
