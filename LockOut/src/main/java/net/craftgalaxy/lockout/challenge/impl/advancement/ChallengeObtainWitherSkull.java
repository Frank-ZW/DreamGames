package net.craftgalaxy.lockout.challenge.impl.advancement;

import net.craftgalaxy.lockout.challenge.types.AbstractAdvancementChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import org.jetbrains.annotations.NotNull;

public class ChallengeObtainWitherSkull extends AbstractAdvancementChallenge {

    public ChallengeObtainWitherSkull(LockOutMinigame lockOut) {
        super(lockOut);
    }

    @Override
    public @NotNull String getDisplayMessage() {
        return "Obtain a Wither Skeleton's skull!";
    }

    @Override
    public @NotNull String getAdvancementKey() {
        return "nether/get_wither_skull";
    }
}
