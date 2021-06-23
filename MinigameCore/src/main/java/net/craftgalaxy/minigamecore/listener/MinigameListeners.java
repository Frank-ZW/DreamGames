package net.craftgalaxy.minigamecore.listener;

import net.craftgalaxy.minigamecore.socket.manager.CoreManager;
import net.craftgalaxy.minigameservice.bukkit.event.MinigameEndEvent;
import net.craftgalaxy.minigameservice.bukkit.event.MinigameStartEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MinigameListeners implements Listener {

    @EventHandler
    public void onMinigameStart(MinigameStartEvent e) {
        CoreManager.getInstance().handleEvent(e, null);
    }

    @EventHandler
    public void onMinigameEnd(MinigameEndEvent e) {
        CoreManager.getInstance().handleEvent(e, null);
    }
}
