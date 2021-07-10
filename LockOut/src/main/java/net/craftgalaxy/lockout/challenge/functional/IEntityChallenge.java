package net.craftgalaxy.lockout.challenge.functional;

import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public interface IEntityChallenge {

    @NotNull EntityType getEntity();
}
