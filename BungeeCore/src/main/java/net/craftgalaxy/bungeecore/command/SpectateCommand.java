package net.craftgalaxy.bungeecore.command;

import net.craftgalaxy.bungeecore.BungeeCore;
import net.craftgalaxy.bungeecore.player.PlayerData;
import net.craftgalaxy.bungeecore.player.manager.PlayerManager;
import net.craftgalaxy.bungeecore.server.manager.ServerManager;
import net.craftgalaxy.minigameservice.bungee.StringUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class SpectateCommand extends Command {

    private final BungeeCore plugin;

    public SpectateCommand(BungeeCore plugin) {
        super("spectate", StringUtil.SPECTATE_COMMAND_PERMISSION);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            commandSender.sendMessage(new TextComponent(StringUtil.PLAYERS_ONLY));
            return;
        }

        ProxiedPlayer sender = (ProxiedPlayer) commandSender;
        if (!this.hasPermission(sender)) {
            sender.sendMessage(new TextComponent(StringUtil.INSUFFICIENT_PERMISSION));
            return;
        }

        PlayerData senderData = PlayerManager.getInstance().getPlayerData(sender);
        if (senderData == null) {
            return;
        }

        if (senderData.isInactive()) {
            if (args.length == 1) {
                ProxiedPlayer player = this.plugin.getProxy().getPlayer(args[0]);
                PlayerData playerData = PlayerManager.getInstance().getPlayerData(player);
                if (playerData == null) {
                    player.sendMessage(new TextComponent(ChatColor.RED + "That player is not online."));
                    return;
                }

                ServerManager.getInstance().handleSpectator(senderData, playerData);
            } else {
                sender.sendMessage(new TextComponent(ChatColor.RED + "To spectate a player, type /spectate <player>"));
            }
        } else {
            sender.sendMessage(new TextComponent(ChatColor.RED + "You cannot run this command whilst queued or in-game."));
        }
    }
}
