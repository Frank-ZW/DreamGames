package net.craftgalaxy.lockout.runnable;

import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class StructureRunnable extends BukkitRunnable {

    private final LockOutMinigame lockOut;

    public StructureRunnable(LockOutMinigame lockOut) {
        this.lockOut = lockOut;
    }

    @Override
    public void run() {
        for (UUID uniqueId : this.lockOut.getTeamsUUID()) {
            Player player = Bukkit.getPlayer(uniqueId);
            if (player == null) {
                continue;
            }

            if (this.lockOut.handleStructureChallenge(player)) {
                this.cancel();
            }
        }
    }
}
