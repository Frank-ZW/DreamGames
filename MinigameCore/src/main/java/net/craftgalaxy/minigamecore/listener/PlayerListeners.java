package net.craftgalaxy.minigamecore.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.craftgalaxy.minigamecore.socket.manager.CoreManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EnderDragonChangePhaseEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.PortalCreateEvent;

public class PlayerListeners implements Listener {

    private final CoreManager manager;

    public PlayerListeners() {
        this.manager = CoreManager.getInstance();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        this.manager.handleConnect(e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        this.manager.handleDisconnect(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncChat(AsyncChatEvent e) {
        this.manager.handleEvent(e, e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        this.manager.handleEvent(e, e.getEntity().getUniqueId());
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            this.manager.handleEvent(e, e.getEntity().getUniqueId());
        }

        if (e.getDamager() instanceof Player) {
            this.manager.handleEvent(e, e.getDamager().getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        this.manager.handleEvent(e, e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        this.manager.handleEvent(e, e.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        this.manager.handleEvent(e, e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent e) {
        this.manager.handleEvent(e, e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onEnderDragonChangePhase(EnderDragonChangePhaseEvent e) {
        this.manager.handleEvent(e, null);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        this.manager.handleEvent(e, e.getEntity().getUniqueId());
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent e) {
        this.manager.handleEvent(e, e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPortalCreate(PortalCreateEvent e) {
        this.manager.handleEvent(e, e.getEntity() == null ? null : e.getEntity().getUniqueId());
    }
}
