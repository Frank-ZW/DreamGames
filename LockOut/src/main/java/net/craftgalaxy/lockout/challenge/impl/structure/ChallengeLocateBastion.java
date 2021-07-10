package net.craftgalaxy.lockout.challenge.impl.structure;

import net.craftgalaxy.lockout.challenge.types.AbstractStructureChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import net.minecraft.server.v1_16_R3.StructureGenerator;
import org.bukkit.StructureType;
import org.jetbrains.annotations.NotNull;

public class ChallengeLocateBastion extends AbstractStructureChallenge {

    public ChallengeLocateBastion(LockOutMinigame lockOut) {
        super(lockOut);
    }

    @Override
    public @NotNull String getDisplayMessage() {
        return "Find a Bastion!";
    }

    @Override
    public @NotNull StructureType getStructureType() {
        return StructureType.BASTION_REMNANT;
    }

    @Override
    public @NotNull StructureGenerator<?> getGenerator() {
        return StructureGenerator.BASTION_REMNANT;
    }
}
