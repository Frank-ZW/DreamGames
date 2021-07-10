package net.craftgalaxy.lockout.challenge.functional;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface IMultiItemChallenge {

    @NotNull Set<Material> getItemTypes();
}
