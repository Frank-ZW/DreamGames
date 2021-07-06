package net.craftgalaxy.minigamecore.command;

import net.craftgalaxy.minigamecore.socket.manager.CoreManager;
import net.craftgalaxy.minigameservice.bukkit.minigame.AbstractMinigame;
import net.craftgalaxy.minigameservice.bukkit.minigame.AbstractSurvivalMinigame;
import net.craftgalaxy.minigameservice.bungee.StringUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SettingsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(StringUtil.PLAYERS_ONLY);
            return true;
        }

        Player sender = (Player) commandSender;
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("seed")) {
                long seed;
                try {
                    seed = Long.parseLong(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "The seed entered must be a number.");
                    return true;
                }

                AbstractMinigame minigame = CoreManager.getInstance().getMinigame();
                if (!(minigame instanceof AbstractSurvivalMinigame)) {
                    sender.sendMessage(ChatColor.RED + "This command can only be run on survival minigames.");
                    return true;
                }

                AbstractSurvivalMinigame survivalGame = (AbstractSurvivalMinigame) minigame;
                survivalGame.setSeed(seed);
                Bukkit.broadcast(Component.text(ChatColor.GREEN + "The seed for the " + survivalGame.getName() + " has been set to " + seed + "."));
            }
        }

        return true;
    }
}
