package net.craftgalaxy.lockout.runnable;

import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import net.craftgalaxy.lockout.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class PlayerTrackerRunnable extends BukkitRunnable {

    private final LockOutMinigame lockOut;

    public PlayerTrackerRunnable(LockOutMinigame lockOut) {
        this.lockOut = lockOut;
    }

    @Override
    public void run() {
        for (Map.Entry<UUID, PlayerData> entry : this.lockOut.getTeamsEntry()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player == null) {
                continue;
            }

            PlayerData playerData = entry.getValue();
            Player target = playerData.getTrackedPlayer();
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && this.lockOut.isPlayerTracker(item)) {
                    this.lockOut.updatePlayerTracker(player, target, item);
                    break;
                }
            }
        }
    }
}
