package net.craftgalaxy.minigamecore.listener;

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent;
import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.craftgalaxy.minigamecore.socket.manager.CoreManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEnterEvent;
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
        if (e.getEntity() instanceof Player) {
            this.manager.handleEvent(e, e.getEntity().getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent e) {
        this.manager.handleEvent(e, e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPortalCreate(PortalCreateEvent e) {
        this.manager.handleEvent(e, e.getEntity() == null ? null : e.getEntity().getUniqueId());
    }

    @EventHandler
    public void onPlayerPickupExperience(PlayerPickupExperienceEvent e) {
        this.manager.handleEvent(e, e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerPickupArrow(PlayerPickupArrowEvent e) {
        this.manager.handleEvent(e, e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerAdvancementCriterionGrant(PlayerAdvancementCriterionGrantEvent e) {
        this.manager.handleEvent(e, e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onEntityPickup(EntityPickupItemEvent e) {
        if (e.getEntity() instanceof Player) {
            this.manager.handleEvent(e, e.getEntity().getUniqueId());
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        this.manager.handleEvent(e, e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        this.manager.handleEvent(e, e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        this.manager.handleEvent(e, e.getWhoClicked().getUniqueId());
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent e) {
        if (e.getEntered() instanceof Player) {
            this.manager.handleEvent(e, e.getEntered().getUniqueId());
        }
    }

    @EventHandler
    public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent e) {
        if (e.getTarget() instanceof Player) {
            this.manager.handleEvent(e, e.getTarget().getUniqueId());
        }
    }

    @EventHandler
    public void onEntityCombust(EntityCombustEvent e) {
        if (e.getEntity() instanceof Player) {
            this.manager.handleEvent(e, e.getEntity().getUniqueId());
        }
    }
}
