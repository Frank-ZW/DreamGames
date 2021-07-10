package net.craftgalaxy.lockout.challenge;

import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractChallenge {

    protected LockOutMinigame lockOut;
    private boolean completed;

    public AbstractChallenge(LockOutMinigame lockOut) {
        this.lockOut = lockOut;
        this.completed = false;
    }

    public void reset() {
        this.lockOut = null;
        this.completed = false;
    }

    public boolean isCompleted() {
        return this.completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public abstract @NotNull String getDisplayMessage();
    public abstract boolean handle(Object object);
}
