package net.craftgalaxy.lockout.challenge.impl.block;

import net.craftgalaxy.lockout.challenge.types.AbstractBlockPlaceChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class ChallengePlaceBeacon extends AbstractBlockPlaceChallenge {

    public ChallengePlaceBeacon(LockOutMinigame lockOut) {
        super(lockOut);
    }

    @Override
    public @NotNull String getDisplayMessage() {
        return "Place a beacon!";
    }

    @Override
    public @NotNull Material getMaterial() {
        return Material.BEACON;
    }
}
