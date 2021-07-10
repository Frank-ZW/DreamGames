package net.craftgalaxy.lockout.challenge.functional;

import net.minecraft.server.v1_16_R3.StructureGenerator;
import org.bukkit.StructureType;
import org.jetbrains.annotations.NotNull;

public interface IStructureChallenge {

    @NotNull StructureType getStructureType();
    @NotNull StructureGenerator<?> getGenerator();
}
