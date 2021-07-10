package net.craftgalaxy.lockout.challenge.impl.movement;

import net.craftgalaxy.lockout.challenge.types.AbstractBiomeChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import org.bukkit.block.Biome;
import org.jetbrains.annotations.NotNull;

public class ChallengeExploreIceSpikedBiome extends AbstractBiomeChallenge {

    public ChallengeExploreIceSpikedBiome(LockOutMinigame lockOut) {
        super(lockOut);
    }

    @Override
    public @NotNull String getDisplayMessage() {
        return "Explore an Ice Spike biome!";
    }

    @Override
    public @NotNull Biome getBiome() {
        return Biome.ICE_SPIKES;
    }
}
