package net.craftgalaxy.minigameservice.bukkit.chat;

import io.papermc.paper.chat.ChatRenderer;
import net.craftgalaxy.minigameservice.bukkit.minigame.AbstractMinigame;
import net.craftgalaxy.minigameservice.bukkit.util.java.StringUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GameChatRenderer implements ChatRenderer {

    private final AbstractMinigame minigame;

    public GameChatRenderer(AbstractMinigame minigame) {
        this.minigame = minigame;
    }

    @Override
    public @NotNull Component render(@NotNull Player player, @NotNull Component component, @NotNull Component message, @NotNull Audience audience) {
        return (this.minigame.getStatus().isInProgress() || this.minigame.getStatus().isFinished() ? (this.minigame.isSpectator(player.getUniqueId()) ? StringUtil.SPECTATOR_PREFIX : StringUtil.GAME_PREFIX) : StringUtil.LOBBY_PREFIX).append(this.minigame.getPlayerChatHandle(player)).append(Component.text(ChatColor.DARK_GRAY.toString() + ChatColor.BOLD + " » " + ChatColor.RESET + ChatColor.WHITE)).append(message);
    }
}
