package net.craftgalaxy.bungeecore.command;

import net.craftgalaxy.minigameservice.bungee.StringUtil;
import net.craftgalaxy.bungeecore.config.ConfigurationManager;
import net.craftgalaxy.bungeecore.player.PlayerData;
import net.craftgalaxy.bungeecore.player.manager.PlayerManager;
import net.craftgalaxy.bungeecore.server.manager.ServerManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class PlayCommand extends Command {

    public PlayCommand() {
        super("play");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            commandSender.sendMessage(new TextComponent(StringUtil.PLAYERS_ONLY));
            return;
        }

        ProxiedPlayer sender = (ProxiedPlayer) commandSender;
        ServerInfo server = sender.getServer().getInfo();
        if (ConfigurationManager.getInstance().isMinigame(server.getName())) {
            return;
        }

        PlayerData senderData = PlayerManager.getInstance().getPlayerData(sender.getUniqueId());
        if (senderData == null) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "An error occurred while retrieving your player profile. Contact an administrator if this occurs."));
            return;
        }

        switch (senderData.getPlayerStatus()) {
            case PLAYING:
                sender.sendMessage(new TextComponent(ChatColor.RED + "You cannot run this command in a minigame."));
                break;
            case SPECTATING:
                sender.sendMessage(new TextComponent(ChatColor.RED + "You cannot run this command while spectating."));
                break;
            default:
                senderData.setPlayerStatus(PlayerData.PlayerStatus.QUEUING);
                if (args.length == 1) {
                    String[] subargs = args[0].trim().toLowerCase().split("_");
                    if (subargs.length != 2) {
                        sender.sendMessage(new TextComponent(ChatColor.RED + "You must specify the name of the minigame and the total number of players."));
                        return;
                    }

                    int maxPlayers;
                    try {
                        maxPlayers = Integer.parseInt(subargs[1]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(new TextComponent(ChatColor.RED + "The number of players entered must be a whole number."));
                        return;
                    }

                    ServerManager.getInstance().queuePlayer(senderData, subargs[0], maxPlayers);
                } else {
                    sender.sendMessage(new TextComponent(ChatColor.RED + "To play a minigame, type /play <minigame name>_<number of players>."));
                }
        }
    }
}
