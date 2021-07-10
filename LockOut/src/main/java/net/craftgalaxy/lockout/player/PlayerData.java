package net.craftgalaxy.lockout.player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PlayerData {

    private final String name;
    private final ChatColor chatColor;
    private final Material icon;
    private UUID tracking;
    private int completed;

    public PlayerData(String name, ChatColor chatColor, Material icon) {
        this.name = name;
        this.chatColor = chatColor;
        this.icon = icon;
        this.tracking = null;
        this.completed = 0;
    }

    public String getName() {
        return this.name;
    }

    public ChatColor getChatColor() {
        return this.chatColor;
    }

    public Material getIcon() {
        return this.icon;
    }

    public UUID getTracking() {
        return this.tracking;
    }

    public void setTracking(UUID tracking) {
        this.tracking = tracking;
    }

    public int getCompleted() {
        return this.completed;
    }

    public PlayerData incrementCompleted() {
        ++this.completed;
        return this;
    }

    @Nullable
    public Player getTrackedPlayer() {
        return this.tracking == null ? null : Bukkit.getPlayer(this.tracking);
    }
}
