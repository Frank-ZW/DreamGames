package net.craftgalaxy.bungeecore;

import net.craftgalaxy.bungeecore.command.PlayCommand;
import net.craftgalaxy.bungeecore.config.ConfigurationManager;
import net.craftgalaxy.bungeecore.listener.PlayerListeners;
import net.craftgalaxy.bungeecore.player.manager.PlayerManager;
import net.craftgalaxy.bungeecore.server.manager.ServerManager;
import net.md_5.bungee.api.plugin.Plugin;

public final class BungeeCore extends Plugin {

    @Override
    public void onEnable() {
        ConfigurationManager.enable(this);
        if (!ConfigurationManager.getInstance().isConnected()) {
            this.onDisable();
            return;
        }

        ServerManager.enable(this);
        PlayerManager.enable(this);
        this.getProxy().getPluginManager().registerListener(this, new PlayerListeners());
        this.getProxy().getPluginManager().registerCommand(this, new PlayCommand());
    }

    @Override
    public void onDisable() {
        this.getProxy().getPluginManager().unregisterCommands(this);
        this.getProxy().getPluginManager().unregisterListeners(this);
        PlayerManager.disable();
        ServerManager.disable();
        ConfigurationManager.disable();
    }
}
