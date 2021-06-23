package net.craftgalaxy.minigameservice.bukkit.util;

import org.bukkit.ChatColor;

public class StringUtil {

    public static final String GAME_PREFIX = ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "Game Chat" + ChatColor.DARK_GRAY + "] ";
    public static final String SPECTATOR_PREFIX = ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "Spectator Chat" + ChatColor.DARK_GRAY + "] ";
    public static final String LOBBY_PREFIX = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_AQUA + "Lobby Chat" + ChatColor.DARK_GRAY + "] ";
    public static final String ERROR_TELEPORTING_TO_LOBBY = ChatColor.RED + "An error occurred while teleporting you to the lobby.";
}
