package net.craftgalaxy.lockout.challenge.impl.misc;

import net.craftgalaxy.lockout.challenge.AbstractChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.jetbrains.annotations.NotNull;

public class ChallengeEnterBed extends AbstractChallenge {

    public ChallengeEnterBed(LockOutMinigame lockOut) {
        super(lockOut);
    }

    @Override
    public @NotNull String getDisplayMessage() {
        return "Lie down in a bed!";
    }

    @Override
    public boolean handle(Object object) {
        if (object instanceof PlayerBedEnterEvent) {
            PlayerBedEnterEvent e = (PlayerBedEnterEvent) object;
            if (e.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.OK) {
                this.lockOut.completeChallenge(e.getPlayer(), this);
                return true;
            }
        }

        return false;
    }
}
