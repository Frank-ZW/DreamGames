package net.craftgalaxy.lockout.challenge.functional;

import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface IMultiEntityChallenge {

    @NotNull Set<EntityType> getEntities();
}
