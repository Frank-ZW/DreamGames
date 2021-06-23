package net.craftgalaxy.minigameservice.bukkit.minigame;

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent;
import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import net.craftgalaxy.minigameservice.bukkit.MinigameService;
import net.craftgalaxy.minigameservice.bukkit.event.MinigameEndEvent;
import net.craftgalaxy.minigameservice.bukkit.runnable.CountdownRunnable;
import net.craftgalaxy.minigameservice.bukkit.util.ItemUtil;
import net.craftgalaxy.minigameservice.bukkit.util.PlayerUtil;
import net.kyori.adventure.text.Component;
import net.milkbowl.vault.chat.Chat;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class AbstractMinigame {

    protected final MinigameService plugin;
    protected final Set<UUID> players = new HashSet<>();
    protected final Set<UUID> spectators = new HashSet<>();
    protected final Random random = new Random();
    protected BukkitRunnable countdown;

    protected final String name;
    protected final Location lobby;
    protected int gameKey;
    protected MinigameStatus status;
    protected long startTimestamp;

    public AbstractMinigame(int gameKey, String name, Location lobby) {
        this.plugin = MinigameService.getInstance();
        this.gameKey = gameKey;
        this.name = name;
        this.lobby = lobby;
        this.status = MinigameStatus.WAITING;
    }

    public int getGameKey() {
        return this.gameKey;
    }

    public MinigameStatus getStatus() {
        return this.status;
    }

    public String getName() {
        return this.name;
    }

    public String getRawName() {
        return this.name.replaceAll("\\s+", "");
    }

    public String getWorldName(World.Environment environment) {
        return StringUtils.capitalize(this.getRawName()) + "_" + this.gameKey + "_" + StringUtils.capitalize(StringUtils.lowerCase(String.valueOf(environment)));
    }

    public int getNumPlayers() {
        return this.players.size();
    }

    public Set<UUID> getPlayers() {
        return this.players;
    }

    public boolean isSpectator(UUID uniqueId) {
        return this.spectators.contains(uniqueId);
    }

    public boolean isPlayer(UUID uniqueId) {
        return this.players.contains(uniqueId);
    }

    public void broadcastTitleAndEffect(String message, Effect effect) {
        for (UUID uniqueId : this.players) {
            Player player = Bukkit.getPlayer(uniqueId);
            if (player != null) {
                player.sendTitle(message, null, 5, 15, 5);
                player.playEffect(player.getLocation(), effect, effect.getData());
            }
        }
    }

    /**
     * Adds the players stored in the queue to the minigame and starts a runnable to display
     * the countdown sequence as a title to the players.
     *
     * @param players   The players queued for the minigame.
     */
    public void startCountdown(@NotNull List<UUID> players) {
        this.status = MinigameStatus.COUNTING_DOWN;
        this.countdown = new CountdownRunnable(this);
        this.countdown.runTaskTimer(this.plugin, 20L, 20L);
        this.players.addAll(players);
    }

    /**
     * Cancels the ongoing countdown and clears the internal cache of all players in the minigame.
     */
    public void cancelCountdown() {
        if (this.countdown == null || this.status != MinigameStatus.COUNTING_DOWN) {
            return;
        }

        this.status = MinigameStatus.WAITING;
        this.countdown.cancel();
        this.broadcastTitleAndEffect(ChatColor.RED + "CANCELLED!", Effect.CLICK2);
        this.spectators.clear();
        this.players.clear();
    }

    /**
     * Clears all data stored in the minigame.
     */
    public void unload() {
        this.spectators.clear();
        this.players.clear();
        this.gameKey = Integer.MIN_VALUE;
        this.startTimestamp = Long.MIN_VALUE;
        Bukkit.getScheduler().cancelTasks(this.plugin);
    }

    /**
     * Removes the player from the minigame.
     *
     * @param player    The player being removed from the minigame.
     */
    public void removePlayer(@NotNull Player player) {
        this.players.remove(player.getUniqueId());
        if (this.spectators.remove(player.getUniqueId())) {
            PlayerUtil.unsetSpectator(player);
            player.teleport(this.lobby);
            this.showSpectator(player);
        }
    }

    public void hideSpectator(@NotNull Player player) {
        for (UUID uniqueId : this.players) {
            Player other = Bukkit.getPlayer(uniqueId);
            if (other == null || this.isSpectator(uniqueId)) {
                continue;
            }

            other.hidePlayer(this.plugin, player);
        }

        this.players.add(player.getUniqueId());
        this.spectators.add(player.getUniqueId());
    }

    public void showSpectator(@NotNull Player player) {
        for (UUID uniqueId : this.players) {
            Player other = Bukkit.getPlayer(uniqueId);
            if (other == null || other.canSee(player)) {
                continue;
            }

            other.showPlayer(this.plugin, player);
        }
    }

    public String getGameDisplayName(@NotNull OfflinePlayer player) {
        return ChatColor.GREEN + player.getName();
    }

    /**
     * @param player        The player typing in chat.
     * @return              A string consisting of the player's prefix if one exists and their in-game display name.
     */
    public String getPlayerChatHandle(@NotNull Player player) {
        String prefix = null;
        Chat chat = this.plugin.getChat();
        if (chat != null) {
            prefix = chat.getPlayerPrefix(player);
        }

        return (prefix == null ? "" : ChatColor.translateAlternateColorCodes('&', prefix) + ChatColor.RESET + " ") + (this.isSpectator(player.getUniqueId()) ? ChatColor.BLUE + player.getName() : this.getGameDisplayName(player));
    }

    public boolean isSpectatorCompass(@Nullable ItemStack item) {
        if (item == null) {
            return false;
        }

        net.minecraft.server.v1_16_R3.ItemStack nms = CraftItemStack.asNMSCopy(item);
        NBTTagCompound compound = nms.getTag();
        return item.getItemMeta() instanceof CompassMeta && compound != null && compound.getBoolean("spectator_compass");
    }

    public void endMinigame(boolean urgently) {
        this.status = MinigameStatus.FINISHED;
        if (urgently) {
            this.endMinigame();
        } else {
            Bukkit.getScheduler().runTaskLater(this.plugin, (Runnable) this::endMinigame, 200L);
        }
    }

    private void endMinigame() {
        this.endTeleport();
        this.deleteWorlds(true);
        Bukkit.getPluginManager().callEvent(new MinigameEndEvent(this));
    }

    public void deleteWorlds(boolean urgently) {
        if (urgently) {
            this.deleteWorlds();
        } else {
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                this.deleteWorlds();
            }, (long) (20 * Math.ceil(1.5 * this.players.size())));
        }
    }

    public void handleSpectatorEvent(@NotNull Event event) {
        if (event instanceof PlayerInteractEvent) {
            PlayerInteractEvent e = (PlayerInteractEvent) event;
            e.setCancelled(true);
            Player player = e.getPlayer();
            Block clicked = e.getClickedBlock();
            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                ItemStack item = e.getItem();
                if (this.isSpectatorCompass(item)) {
                    Inventory inventory = Bukkit.createInventory(null, 36, ItemUtil.SPECTATOR_GUI);
                    for (UUID uniqueId : this.players) {
                        if (this.isSpectator(uniqueId)) {
                            continue;
                        }

                        OfflinePlayer offline = Bukkit.getOfflinePlayer(uniqueId);
                        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                        SkullMeta meta = (SkullMeta) head.getItemMeta();
                        if (meta != null) {
                            meta.displayName(Component.text(this.getGameDisplayName(offline)));
                            meta.setOwningPlayer(offline);
                            head.setItemMeta(meta);
                        }

                        inventory.addItem(head);
                    }

                    player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                    player.openInventory(inventory);
                    return;
                }
            }

            if (e.getAction() == Action.RIGHT_CLICK_BLOCK && clicked != null && clicked.getState() instanceof Chest) {
                Chest chest = (Chest) clicked.getState();
                player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                player.openInventory(chest.getInventory());
            }
        } else if (event instanceof PlayerPickupExperienceEvent) {
            ((PlayerPickupExperienceEvent) event).setCancelled(true);
        } else if (event instanceof PlayerPickupArrowEvent) {
            ((PlayerPickupArrowEvent) event).setCancelled(true);
        } else if (event instanceof PlayerAdvancementCriterionGrantEvent) {
            ((PlayerAdvancementCriterionGrantEvent) event).setCancelled(true);
        } else if (event instanceof EntityPickupItemEvent) {
            ((EntityPickupItemEvent) event).setCancelled(true);
        } else if (event instanceof PlayerDeathEvent) {
            ((PlayerDeathEvent) event).setCancelled(true);
        } else if (event instanceof PlayerDropItemEvent) {
            ((PlayerDropItemEvent) event).setCancelled(true);
        } else if (event instanceof EntityDamageEvent) {
            ((EntityDamageEvent) event).setCancelled(true);
        } else if (event instanceof BlockPlaceEvent) {
            ((BlockPlaceEvent) event).setCancelled(true);
        } else if (event instanceof BlockBreakEvent) {
            ((BlockBreakEvent) event).setCancelled(true);
        } else if (event instanceof InventoryClickEvent) {
            InventoryClickEvent e = (InventoryClickEvent) event;
            e.setCancelled(true);
            if (e.getView().title().equals(ItemUtil.SPECTATOR_GUI)) {
                ItemStack clicked = e.getCurrentItem();
                if (clicked != null && clicked.getType() == Material.PLAYER_HEAD) {
                    Player spectated = Bukkit.getPlayer(ChatColor.stripColor(clicked.getItemMeta().getDisplayName()));
                    if (spectated == null) {
                        return;
                    }

                    Player clicker = (Player) e.getWhoClicked();
                    clicker.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                    clicker.teleportAsync(spectated.getLocation()).thenAccept(result -> {
                        if (result) {
                            clicker.sendMessage(ChatColor.GREEN + "You are now spectating " + spectated.getName());
                        } else {
                            clicker.sendMessage(ChatColor.RED + "Failed to teleport you to " + spectated.getName());
                        }
                    });
                }
            }
        } else if (event instanceof VehicleEnterEvent) {
            ((VehicleEnterEvent) event).setCancelled(true);
        } else if (event instanceof EntityTargetLivingEntityEvent) {
            ((EntityTargetLivingEntityEvent) event).setCancelled(true);
        } else if (event instanceof EntityCombustEvent) {
            ((EntityCombustEvent) event).setCancelled(true);
        }
    }

    /**
     * Handles the starting teleportation logic per player given the location to be teleported to. The spawn location
     * of the player is determined through a rotation by a certain angular difference that is calculated from the
     * number of players in the game. The angle increments to rotate the spawn location for the next player.
     *
     * @param player    The player being teleported at the start of the match.
     * @param to        The location the player is being teleported to.
     * @return          True if the angle should be incremented, false otherwise.
     */
    protected abstract boolean onPlayerStartTeleport(@NotNull Player player, @NotNull Location to);

    /**
     * Handles player actions for player teleportation at the end of the match.
     *
     * @param player    The player being teleported at the end of the match.
     */
    protected abstract void onPlayerEndTeleport(@NotNull Player player);

    /**
     * Returns the message sent to the players at the start of the game after the player has been teleported.
     *
     * @param uniqueId  The UUID of the player to be sent the message.
     * @return          The message that should be sent to the player at the start of the match describing their role.
     */
    protected abstract String getStartMessage(@NotNull UUID uniqueId);

    /**
     * Teleports all players in the match to a designated spawn point. Each player action during start teleportation is
     * handled using {@link #onPlayerStartTeleport(Player, Location)}.
     */
    public abstract void startTeleport();

    public abstract void endTeleport();
    public abstract boolean createWorlds();
    public abstract boolean worldsLoaded();
    public abstract void deleteWorlds();
    public abstract void handleEvent(Event event);

    public enum MinigameStatus {
        WAITING,
        COUNTING_DOWN,
        IN_PROGRESS,
        FINISHED;

        MinigameStatus() {}

        public boolean isInProgress() {
            return this == MinigameStatus.IN_PROGRESS;
        }

        public boolean isFinished() {
            return this == MinigameStatus.FINISHED;
        }
    }
}
