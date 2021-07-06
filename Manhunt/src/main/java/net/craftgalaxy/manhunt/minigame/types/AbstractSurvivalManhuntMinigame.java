package net.craftgalaxy.manhunt.minigame.types;

import net.craftgalaxy.minigameservice.bukkit.util.minecraft.ItemUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EnderDragonChangePhaseEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

public abstract class AbstractSurvivalManhuntMinigame extends AbstractManhuntMinigame {

    public AbstractSurvivalManhuntMinigame(int gameKey, Location lobby) {
        super(gameKey, lobby);
    }

    @Override
    public void handleEvent(Event event) {
        super.handleEvent(event);
        if (event instanceof PlayerEvent) {
            Player player = ((PlayerEvent) event).getPlayer();
            if (event instanceof PlayerInteractEvent) {
                PlayerInteractEvent e = (PlayerInteractEvent) event;
                Block clicked = e.getClickedBlock();
                if (this.isHunter(player.getUniqueId()) && (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) && e.getItem() != null && this.isPlayerTracker(e.getItem())) {
                    this.updatePlayerTracker(player, e.getItem());
                }

                if (e.getAction() == Action.RIGHT_CLICK_BLOCK && player.getWorld().getEnvironment() == World.Environment.NETHER && ItemUtil.isBed(clicked) && !this.bedBombing) {
                    e.setCancelled(true);
                    player.sendMessage(Component.newline().append(Component.text(ChatColor.RED + "Bed bombing has been disabled. If you believe this is a mistake, submit a request for bed bombing to be enabled.").append(Component.newline())));
                }
            } else if (event instanceof PlayerDropItemEvent) {
                PlayerDropItemEvent e = (PlayerDropItemEvent) event;
                if (this.isHunter(player.getUniqueId())) {
                    ItemStack drop = e.getItemDrop().getItemStack();
                    if (this.isPlayerTracker(drop)) {
                        e.setCancelled(true);
                        player.sendMessage(Component.text(ChatColor.RED + "You cannot drop your Player Tracker!"));
                    }
                }
            } else if (event instanceof PlayerRespawnEvent) {
                if (this.isHunter(player.getUniqueId())) {
                    player.getInventory().setItem(8, ItemUtil.createPlayerTracker(ItemUtil.MANHUNT_PLAYER_TRACKER));
                }
            } else if (event instanceof PlayerAdvancementDoneEvent) {
                this.addAwardedAdvancement(player, ((PlayerAdvancementDoneEvent) event).getAdvancement());
            }
        } else if (event instanceof PlayerDeathEvent) {
            PlayerDeathEvent e = (PlayerDeathEvent) event;
            if (this.isSpeedrunner(e.getEntity().getUniqueId()) && this.status.isInProgress()) {
                this.endMinigame(false, false);
            } else {
                e.getDrops().removeIf(this::isPlayerTracker);
            }
        } else if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
            if (!(e.getDamager() instanceof Player) || e.isCancelled()) {
                return;
            }

            if (e.getEntity() instanceof Player && TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - this.startTimestamp) <= 5) {
                e.setCancelled(true);
            }
        } else if (event instanceof EnderDragonChangePhaseEvent) {
            EnderDragonChangePhaseEvent e = (EnderDragonChangePhaseEvent) event;
            if (e.getNewPhase() == EnderDragon.Phase.DYING) {
                this.endMinigame(true, false);
            }
        }
    }
}
