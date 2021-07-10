package net.craftgalaxy.lockout.challenge.impl.advancement;

import net.craftgalaxy.lockout.challenge.types.AbstractAdvancementChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import org.jetbrains.annotations.NotNull;

public class ChallengeObtainDiamonds extends AbstractAdvancementChallenge {

    public ChallengeObtainDiamonds(LockOutMinigame lockOut) {
        super(lockOut);
    }

    @Override
    public @NotNull String getDisplayMessage() {
        return "Find diamonds!";
    }

    @Override
    public @NotNull String getAdvancementKey() {
        return "story/mine_diamond";
    }
}
