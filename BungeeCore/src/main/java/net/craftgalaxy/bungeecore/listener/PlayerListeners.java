package net.craftgalaxy.bungeecore.listener;

import net.craftgalaxy.bungeecore.config.ConfigurationManager;
import net.craftgalaxy.bungeecore.player.PlayerData;
import net.craftgalaxy.bungeecore.player.manager.PlayerManager;
import net.craftgalaxy.bungeecore.server.ServerData;
import net.craftgalaxy.bungeecore.server.manager.ServerManager;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerListeners implements Listener {

    @EventHandler
    public void onPlayerLogin(PostLoginEvent e) {
        PlayerManager.getInstance().addPlayer(e.getPlayer());
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent e) {
        PlayerManager.getInstance().removePlayer(e.getPlayer());
    }

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent e) {
        if (e.getFrom() == null) {
            return;
        }

        ServerData serverData = ServerManager.getInstance().getServerData(e.getFrom());
        if (serverData != null) {
            serverData.checkShouldDisconnect(e.getPlayer());
        }
    }

    @EventHandler
    public void onServerConnect(ServerConnectEvent e) {
        if (ConfigurationManager.getInstance().isMinigame(e.getTarget().getName())) {
            return;
        }

        ProxiedPlayer player = e.getPlayer();
        ServerInfo server = PlayerManager.getInstance().removeDisconnection(player.getUniqueId());
        PlayerData playerData = PlayerManager.getInstance().getPlayerData(player.getUniqueId());
        if (server != null && playerData != null) {
            e.setTarget(server);
            playerData.setPlayerStatus(PlayerData.PlayerStatus.PLAYING);
        }
    }
}
