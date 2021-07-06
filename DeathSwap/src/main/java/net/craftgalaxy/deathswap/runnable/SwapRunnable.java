package net.craftgalaxy.deathswap.runnable;

import com.google.common.collect.ImmutableSet;
import net.craftgalaxy.deathswap.minigame.DeathSwapMinigame;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

public class SwapRunnable extends BukkitRunnable {

    private final DeathSwapMinigame deathSwap;
    private final Set<Integer> timestamps = ImmutableSet.of(10, 9, 8, 7, 6, 5, 4, 3, 2, 1);
    private int countdown;

    public SwapRunnable(DeathSwapMinigame deathSwap) {
        this.deathSwap = deathSwap;
        this.countdown = 3 * 60;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        if (this.timestamps.contains(this.countdown)) {
            if (this.countdown == 10) {
                Bukkit.broadcast(Component.newline());
            }

            Bukkit.broadcast(Component.text(ChatColor.RED + "Swapping in " + this.countdown + " second" + (this.countdown == 1 ? "" : "s") + "!"));
        }

        if (this.countdown <= 0) {
            this.deathSwap.swapPlayers();
            this.countdown = 3 * 60;
        } else {
            --this.countdown;
        }
    }
}
