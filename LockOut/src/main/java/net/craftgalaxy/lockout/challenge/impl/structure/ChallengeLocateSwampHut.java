package net.craftgalaxy.lockout.challenge.impl.structure;

import net.craftgalaxy.lockout.challenge.types.AbstractStructureChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import net.minecraft.server.v1_16_R3.StructureGenerator;
import org.bukkit.StructureType;
import org.jetbrains.annotations.NotNull;

public class ChallengeLocateSwampHut extends AbstractStructureChallenge {

    public ChallengeLocateSwampHut(LockOutMinigame lockOut) {
        super(lockOut);
    }

    @Override
    public @NotNull String getDisplayMessage() {
        return "Find a Swamp Hut!";
    }

    @Override
    public @NotNull StructureType getStructureType() {
        return StructureType.SWAMP_HUT;
    }

    @Override
    public @NotNull StructureGenerator<?> getGenerator() {
        return StructureGenerator.SWAMP_HUT;
    }
}
