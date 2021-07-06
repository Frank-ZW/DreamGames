package net.craftgalaxy.bungeecore.command;

import net.craftgalaxy.bungeecore.server.manager.ServerManager;
import net.craftgalaxy.minigameservice.bungee.StringUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class ForceEndCommand extends Command {

    public ForceEndCommand() {
        super("forceend", StringUtil.FORCE_END_COMMAND_PERMISSION);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!this.hasPermission(sender)) {
            sender.sendMessage(new TextComponent(StringUtil.INSUFFICIENT_PERMISSION));
            return;
        }

        if (args.length == 1) {
            try {
                int gameKey = Integer.parseInt(args[0]);
                if (ServerManager.getInstance().forceEnd(gameKey)) {
                    sender.sendMessage(new TextComponent(ChatColor.GREEN + "A minigame with a game key of " + gameKey + " has been forcefully ended."));
                } else {
                    sender.sendMessage(new TextComponent(ChatColor.RED + "There are no servers hosting a minigame with a game key of " + gameKey + "."));
                }
            } catch (NumberFormatException e) {
                if (ServerManager.getInstance().forceEnd(args[0])) {
                    sender.sendMessage(new TextComponent(ChatColor.GREEN + "If there was a minigame on " + args[0].toLowerCase() + ", it has been forcefully ended."));
                } else {
                    sender.sendMessage(new TextComponent(ChatColor.RED + "Failed to find a server loaded into the proxy with that name."));
                }
            }
        } else {
            sender.sendMessage(new TextComponent(ChatColor.RED + "To forcibly end an ongoing minigame, type /forceend <game key> or /forceend <server>"));
        }
    }
}
