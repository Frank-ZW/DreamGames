package net.craftgalaxy.minigameservice.bukkit.chat;

import io.papermc.paper.chat.ChatRenderer;
import net.craftgalaxy.minigameservice.bukkit.util.java.StringUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LobbyChatRenderer implements ChatRenderer {

    @Override
    public @NotNull Component render(@NotNull Player player, @NotNull Component component, @NotNull Component component1, @NotNull Audience audience) {
        return StringUtil.LOBBY_PREFIX.append(component).append(Component.text(ChatColor.DARK_GRAY.toString() + ChatColor.BOLD + " » " + ChatColor.RESET)).append(component1);
    }
}
