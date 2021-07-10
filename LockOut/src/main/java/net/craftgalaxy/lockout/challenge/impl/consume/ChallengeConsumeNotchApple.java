package net.craftgalaxy.lockout.challenge.impl.consume;

import net.craftgalaxy.lockout.challenge.types.AbstractConsumeChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class ChallengeConsumeNotchApple extends AbstractConsumeChallenge {

    public ChallengeConsumeNotchApple(LockOutMinigame lockOut) {
        super(lockOut);
    }

    @Override
    public @NotNull String getDisplayMessage() {
        return "Consume an enchanted golden apple!";
    }

    @Override
    public @NotNull Material getItemType() {
        return Material.ENCHANTED_GOLDEN_APPLE;
    }
}
