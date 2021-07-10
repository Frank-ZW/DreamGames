package net.craftgalaxy.minigameservice.bukkit.minigame.functional;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IInteractiveBoard<K> {

    void updateBoard();
    void setGuiIcon(int index, @NotNull ItemStack item);
    void setGuiIndex(K key, int index);
    void openBoardGui(@NotNull Player player);
    boolean isBoardIcon(@NotNull ItemStack item);
    int getGuiIndex(K key);
    @NotNull ItemStack createBoardGui();
    @Nullable ItemStack getGuiIcon(int index);
}
