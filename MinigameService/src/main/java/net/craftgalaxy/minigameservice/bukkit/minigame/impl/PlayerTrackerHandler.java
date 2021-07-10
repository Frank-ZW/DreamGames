package net.craftgalaxy.minigameservice.bukkit.minigame.impl;

import net.craftgalaxy.minigameservice.bukkit.minigame.functional.IPlayerTracker;
import net.craftgalaxy.minigameservice.bukkit.util.minecraft.ItemUtil;
import net.kyori.adventure.text.Component;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerTrackerHandler implements IPlayerTracker {

    private final Component displayName;
    private final String compoundTag;

    public PlayerTrackerHandler(Component displayName, String compoundTag) {
        this.displayName = displayName;
        this.compoundTag = compoundTag;
    }

    @Override
    public boolean isPlayerTracker(@NotNull ItemStack item) {
        net.minecraft.server.v1_16_R3.ItemStack nms = CraftItemStack.asNMSCopy(item);
        NBTTagCompound compound = nms.getTag();
        return compound != null && compound.getBoolean(this.compoundTag) && item.getItemMeta() instanceof CompassMeta;
    }

    @Override
    public void updatePlayerTracker(@NotNull Player player, @Nullable Player target, @NotNull ItemStack tracker) {
        if (target == null) {
            player.sendActionBar(Component.text(ChatColor.RED + "There are no players to track!"));
            return;
        }

        CompassMeta meta = (CompassMeta) tracker.getItemMeta();
        if (player.getWorld().equals(target.getWorld())) {
            meta.setLodestone(target.getLocation());
            meta.setLodestoneTracked(false);
            tracker.setItemMeta(meta);
            player.sendActionBar(Component.text(ChatColor.GREEN + "Currently tracking " + target.getName() + "'s latest location."));
        } else {
            player.sendActionBar(Component.text(ChatColor.RED + "There are no players to track!"));
        }
    }

    @Override
    public @NotNull ItemStack createPlayerTracker() {
        return ItemUtil.createTaggedItem(Material.COMPASS, this.displayName, this.compoundTag);
    }
}
