package net.craftgalaxy.minigameservice.bukkit.minigame.impl;

import net.craftgalaxy.minigameservice.bukkit.minigame.functional.IInteractiveBoard;
import net.craftgalaxy.minigameservice.bukkit.util.minecraft.ItemUtil;
import net.kyori.adventure.text.Component;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class InteractiveBoardHandler<K> implements IInteractiveBoard<K> {

    private final Inventory board;
    private final Material boardIcon;
    private final Component displayName;
    private final String compoundTag;
    private final Map<K, Integer> guiIndices = new HashMap<>();

    public InteractiveBoardHandler(Material boardIcon, Component title, Component displayName, int boardHeight, String compoundTag) {
        this.boardIcon = boardIcon;
        this.displayName = displayName;
        this.compoundTag = compoundTag;
        this.board = Bukkit.createInventory(null, boardHeight * 9, title);
    }

    @Override
    public void openBoardGui(@NotNull Player player) {
        player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
        player.openInventory(this.board);
    }

    @Override
    public boolean isBoardIcon(@NotNull ItemStack item) {
        net.minecraft.server.v1_16_R3.ItemStack nms = CraftItemStack.asNMSCopy(item);
        NBTTagCompound compound = nms.getTag();
        return compound != null && compound.getBoolean(this.compoundTag) && item.getType() == this.boardIcon;
    }

    @Override
    public int getGuiIndex(K key) {
        return this.guiIndices.get(key);
    }

    @Override
    public @NotNull ItemStack createBoardGui() {
        return ItemUtil.createTaggedItem(this.boardIcon, this.displayName, this.compoundTag);
    }

    @Override
    public @Nullable ItemStack getGuiIcon(int index) {
        return this.board.getItem(index);
    }

    @Override
    public void updateBoard() {
        this.board.getViewers().forEach(v -> ((Player) v).updateInventory());
    }

    @Override
    public void setGuiIcon(int index, @NotNull ItemStack item) {
        this.board.setItem(index, item);
    }

    @Override
    public void setGuiIndex(K key, int index) {
        this.guiIndices.put(key, index);
    }
}
