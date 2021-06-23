package net.craftgalaxy.minigameservice.bungee;

import net.md_5.bungee.api.ChatColor;

public class StringUtil {

    public static final String PLAYERS_ONLY = ChatColor.RED + "You must be a player to run thi command.";
    public static final String INSUFFICIENT_PERMISSION = ChatColor.RED + "You do not have permission to run this command.";

    public static final String SOLO_COMMAND_PERMISSION = "bungeecore.command.%s.solo";
}
