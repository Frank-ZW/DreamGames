package net.craftgalaxy.lockout.challenge.impl.entity;

import net.craftgalaxy.lockout.challenge.types.AbstractEntityDeathChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public class ChallengeKillSlime extends AbstractEntityDeathChallenge {

    public ChallengeKillSlime(LockOutMinigame lockOut) {
        super(lockOut);
    }

    @Override
    public @NotNull String getDisplayMessage() {
        return "Kill a Slime!";
    }

    @Override
    public @NotNull EntityType getEntity() {
        return EntityType.SLIME;
    }
}
