package net.craftgalaxy.lockout.challenge.impl.structure;

import net.craftgalaxy.lockout.challenge.types.AbstractStructureChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import net.minecraft.server.v1_16_R3.StructureGenerator;
import org.bukkit.StructureType;
import org.jetbrains.annotations.NotNull;

public class ChallengeLocateStronghold extends AbstractStructureChallenge {

    public ChallengeLocateStronghold(LockOutMinigame lockOut) {
        super(lockOut);
    }

    @Override
    public @NotNull String getDisplayMessage() {
        return "Find a Stronghold!";
    }

    @Override
    public @NotNull StructureType getStructureType() {
        return StructureType.STRONGHOLD;
    }

    @Override
    public @NotNull StructureGenerator<?> getGenerator() {
        return StructureGenerator.STRONGHOLD;
    }
}
