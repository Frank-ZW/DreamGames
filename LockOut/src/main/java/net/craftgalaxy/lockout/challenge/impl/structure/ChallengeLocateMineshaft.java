package net.craftgalaxy.lockout.challenge.impl.structure;

import net.craftgalaxy.lockout.challenge.types.AbstractStructureChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import net.minecraft.server.v1_16_R3.StructureGenerator;
import org.bukkit.StructureType;
import org.jetbrains.annotations.NotNull;

public class ChallengeLocateMineshaft extends AbstractStructureChallenge {

    public ChallengeLocateMineshaft(LockOutMinigame lockOut) {
        super(lockOut);
    }

    @Override
    public @NotNull String getDisplayMessage() {
        return "Find an abandoned mineshaft!";
    }

    @Override
    public @NotNull StructureType getStructureType() {
        return StructureType.MINESHAFT;
    }

    @Override
    public @NotNull StructureGenerator<?> getGenerator() {
        return StructureGenerator.MINESHAFT;
    }
}
