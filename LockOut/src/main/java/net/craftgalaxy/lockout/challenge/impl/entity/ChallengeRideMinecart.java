package net.craftgalaxy.lockout.challenge.impl.entity;

import net.craftgalaxy.lockout.challenge.AbstractChallenge;
import net.craftgalaxy.lockout.challenge.functional.IEntityChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.jetbrains.annotations.NotNull;

public class ChallengeRideMinecart extends AbstractChallenge implements IEntityChallenge {

    public ChallengeRideMinecart(LockOutMinigame lockOut) {
        super(lockOut);
    }

    @Override
    public @NotNull String getDisplayMessage() {
        return "Ride a minecart!";
    }

    @Override
    public boolean handle(Object object) {
        if (object instanceof VehicleEnterEvent) {
            VehicleEnterEvent e = (VehicleEnterEvent) object;
            if (e.getVehicle().getType() == this.getEntity() && e.getEntered() instanceof Player) {
                this.lockOut.completeChallenge((Player) e.getEntered(), this);
                return true;
            }
        }

        return false;
    }

    @Override
    public @NotNull EntityType getEntity() {
        return EntityType.MINECART;
    }
}
