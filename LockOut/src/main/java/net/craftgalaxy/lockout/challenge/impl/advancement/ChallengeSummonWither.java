package net.craftgalaxy.lockout.challenge.impl.advancement;

import net.craftgalaxy.lockout.challenge.types.AbstractAdvancementChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import org.jetbrains.annotations.NotNull;

public class ChallengeSummonWither extends AbstractAdvancementChallenge {

    public ChallengeSummonWither(LockOutMinigame lockOut) {
        super(lockOut);
    }

    @Override
    public @NotNull String getDisplayMessage() {
        return "Summon the Wither!";
    }

    @Override
    public @NotNull String getAdvancementKey() {
        return "nether/summon_wither";
    }
}
