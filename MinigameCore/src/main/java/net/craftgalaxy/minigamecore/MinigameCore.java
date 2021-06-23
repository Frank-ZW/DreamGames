package net.craftgalaxy.minigamecore;

import net.craftgalaxy.minigamecore.listener.MinigameListeners;
import net.craftgalaxy.minigamecore.listener.PlayerListeners;
import net.craftgalaxy.minigamecore.socket.manager.CoreManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public final class MinigameCore extends JavaPlugin {

    private Location lobbyLocation;
    private String serverName;
    private String hostName;
    private int port;
    private boolean initialStartup;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        if (this.readConfig()) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if (this.initialStartup) {
            Bukkit.getLogger().info(ChatColor.GREEN + "Before this plugin can be deployed, first enter the name of the server as it appears in the proxy as well as make any necessary changes to the port and host name under socket settings. Restart the server once those changes have been made.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        CoreManager.enable(this);
        Bukkit.getPluginManager().registerEvents(new PlayerListeners(), this);
        Bukkit.getPluginManager().registerEvents(new MinigameListeners(), this);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        CoreManager.disable();
        this.writeConfig();
    }

    private boolean readConfig() {
        try {
            String name = this.getConfig().getString("lobby-location.world-name");
            if (name == null) {
                Bukkit.getLogger().warning("Failed to retrieve world name for the lobby.");
                return true;
            }

            World world = Bukkit.getWorld(name);
            if (world == null) {
                Bukkit.getLogger().warning("Failed to retrieve a world by the name of " + name + ". Has it been loaded into memory?");
                return true;
            }

            double x = this.getConfig().getDouble("lobby-location.X");
            double y = this.getConfig().getDouble("lobby-location.Y");
            double z = this.getConfig().getDouble("lobby-location.Z");
            if (y < 0.0D) {
                y = world.getHighestBlockYAt((int) x, (int) z) + 1;
            }

            this.lobbyLocation = new Location(world, x, y, z);
            this.serverName = this.getConfig().getString("socket-settings.proxy-side-server-name");
            this.hostName = this.getConfig().getString("socket-settings.host-name");
            this.port = this.getConfig().getInt("socket-settings.port-number");
            this.initialStartup = this.getConfig().getBoolean("initial-startup");
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    private void writeConfig() {
        this.getConfig().set("lobby-location.world-name", this.lobbyLocation.getWorld().getName());
        this.getConfig().set("lobby-location.X", this.lobbyLocation.getX());
        this.getConfig().set("lobby-location.Y", this.lobbyLocation.getY());
        this.getConfig().set("lobby-location.Z", this.lobbyLocation.getZ());
        this.getConfig().set("socket-settings.proxy-side-server-name", this.serverName);
        this.getConfig().set("socket-settings.host-name", this.hostName);
        this.getConfig().set("socket-settings.port-number", this.port);
        this.getConfig().set("initial-startup", false);
        this.saveConfig();
    }

    public String getServerName() {
        return this.serverName;
    }

    public String getHostName() {
        return this.hostName;
    }

    public int getPort() {
        return this.port;
    }

    public Location getLobbyLocation() {
        return this.lobbyLocation;
    }
}
