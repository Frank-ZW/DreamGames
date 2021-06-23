package net.craftgalaxy.minigamecore.runnable;

import net.craftgalaxy.minigamecore.MinigameCore;
import net.craftgalaxy.minigamecore.socket.manager.CoreManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;

public class PendingConnectionRunnable extends BukkitRunnable {

    private final MinigameCore plugin;

    public PendingConnectionRunnable(MinigameCore plugin) {
        this.plugin = plugin;
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
        try {
            if (CoreManager.getInstance().searchAvailableConnections()) {
                Bukkit.getLogger().info(ChatColor.GREEN + "Established TCP socket connection for " + this.plugin.getServerName() + " on port " + this.plugin.getPort() + ".");
                this.cancel();
            }
        } catch (IOException e) {
            Bukkit.getLogger().warning("Failed to establish TCP socket connection with the proxy. Another attempt will be made in ten seconds...");
        }
    }
}
