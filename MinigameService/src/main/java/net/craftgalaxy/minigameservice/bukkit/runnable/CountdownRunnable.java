package net.craftgalaxy.minigameservice.bukkit.runnable;

import com.google.common.collect.ImmutableSet;
import net.craftgalaxy.minigameservice.bukkit.minigame.AbstractMinigame;
import net.craftgalaxy.minigameservice.bukkit.event.MinigameStartEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

public class CountdownRunnable extends BukkitRunnable {

    private final AbstractMinigame minigame;
    private int countdown;
    private final Set<Integer> timestamps = ImmutableSet.of(15, 10, 5, 4, 3, 2, 1);

    public CountdownRunnable(AbstractMinigame minigame) {
        this.minigame = minigame;
        this.countdown = 15;
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
            this.minigame.broadcastTitleAndEffect(this.translateCountdownColor(this.countdown), Effect.CLICK2);
        }

        if (this.countdown <= 0) {
            Bukkit.getPluginManager().callEvent(new MinigameStartEvent(this.minigame));
            this.minigame.startTeleport();
            this.cancel();
            return;
        }

        --this.countdown;
    }

    public String translateCountdownColor(int countdown) {
        if (countdown > 4) {
            return ChatColor.GREEN.toString() + countdown;
        } else if (countdown > 2) {
            return ChatColor.GOLD.toString() + countdown;
        } else {
            return ChatColor.RED.toString() + countdown;
        }
    }
}
