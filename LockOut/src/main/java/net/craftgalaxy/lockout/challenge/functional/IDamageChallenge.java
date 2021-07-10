package net.craftgalaxy.lockout.challenge.functional;

import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

public interface IDamageChallenge {

    @NotNull EntityDamageEvent.DamageCause getDamageCause();
}
