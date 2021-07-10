package net.craftgalaxy.lockout.challenge.impl.consume;

import net.craftgalaxy.lockout.challenge.types.AbstractConsumeChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class ChallengeConsumeSuspiciousStew extends AbstractConsumeChallenge {

    public ChallengeConsumeSuspiciousStew(LockOutMinigame lockOut) {
        super(lockOut);
    }

    @Override
    public @NotNull String getDisplayMessage() {
        return "Consume a suspicious stew!";
    }

    @Override
    public @NotNull Material getItemType() {
        return Material.SUSPICIOUS_STEW;
    }
}
