package net.craftgalaxy.lockout.challenge.impl.structure;

import net.craftgalaxy.lockout.challenge.types.AbstractStructureChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import net.minecraft.server.v1_16_R3.StructureGenerator;
import org.bukkit.StructureType;
import org.jetbrains.annotations.NotNull;

public class StructureLocateTemple extends AbstractStructureChallenge {

    public StructureLocateTemple(LockOutMinigame lockOut) {
        super(lockOut);
    }

    @Override
    public @NotNull String getDisplayMessage() {
        return "Find a Jungle Temple!";
    }

    @Override
    public @NotNull StructureType getStructureType() {
        return StructureType.JUNGLE_PYRAMID;
    }

    @Override
    public @NotNull StructureGenerator<?> getGenerator() {
        return StructureGenerator.JUNGLE_PYRAMID;
    }
}
