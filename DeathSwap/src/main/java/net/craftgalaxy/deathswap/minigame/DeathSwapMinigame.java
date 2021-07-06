package net.craftgalaxy.deathswap.minigame;

import net.craftgalaxy.deathswap.runnable.SwapRunnable;
import net.craftgalaxy.minigameservice.bukkit.minigame.AbstractSurvivalMinigame;
import net.craftgalaxy.minigameservice.bukkit.util.minecraft.PlayerUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DeathSwapMinigame extends AbstractSurvivalMinigame {

    private final List<UUID> alive = new ArrayList<>();
    private BukkitRunnable swapRunnable;

    public DeathSwapMinigame(int gameKey, Location lobby) {
        super(gameKey, "Death Swap", lobby, false, false);
    }

    @Override
    public boolean createWorlds() {
        if (this.getOverworld() != null) {
            return true;
        }

        World.Environment environment = World.Environment.NORMAL;
        World world = new WorldCreator(this.getWorldName(environment)).type(this.worldType).createWorld();
        if (world != null) {
            world.setAutoSave(false);
            world.setKeepSpawnInMemory(false);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            this.worlds.put(environment, world);
        }

        return this.getOverworld() != null;
    }

    @Override
    public void deleteWorlds() {
        super.deleteWorlds();
        this.alive.clear();
        if (this.swapRunnable != null && !this.swapRunnable.isCancelled()) {
            this.swapRunnable.cancel();
            this.swapRunnable = null;
        }
    }

    /**
     * Adds the players stored in the queue to the minigame and starts a runnable to display
     * the countdown sequence as a title to the players.
     *
     * @param players The players queued for the minigame.
     */
    @Override
    public void startCountdown(@NotNull List<UUID> players) {
        super.startCountdown(players);
        this.alive.addAll(players);
    }

    @Override
    public void cancelCountdown() {
        super.cancelCountdown();
        this.alive.clear();
    }

    @Override
    public void startTeleport() {
        super.startTeleport();
        this.swapRunnable = new SwapRunnable(this);
        this.swapRunnable.runTaskTimer(this.plugin, 0L, 20L);
    }

    /**
     * Returns the message sent to the players at the start of the game after the player has been teleported.
     *
     * @param uniqueId The UUID of the player to be sent the message.
     * @return The message that should be sent to the player at the start of the match describing their role.
     */
    @Override
    protected String getStartMessage(@NotNull UUID uniqueId) {
        return ChatColor.GREEN + "You must kill the other players in your " + this.getName() + " using your surroundings. PVP has been disabled.";
    }

    /**
     * Handles swapping players across the map. Since at any given time, a player might be offline, the
     * method first streams through the player list and filters out those that are offline before collecting
     * the remaining players into a list.
     * <p>
     * If the list is empty or has one player, then the server ends the
     * Death Swap. Otherwise, the server loops through the list of online
     * players and teleports them to the player stored adjacently to them
     * on the right
     */
    public void swapPlayers() {
        if (this.alive.isEmpty() || this.alive.size() == 1) {
            return;
        }

        List<Player> online = this.alive.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toList());
        if (online.size() > 1) {
            Player initial = online.remove(this.random.nextInt(online.size()));
            Location loc = initial.getLocation();
            do {
                Player next = online.remove(this.random.nextInt(online.size()));
                initial.teleport(next == null ? loc : next.getLocation());
                initial.playSound(initial.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.75F, 0.75F);
                initial = next;
            } while (!online.isEmpty());
        }
    }

    /**
     * Removes the player from the minigame.
     *
     * @param player The player being removed from the minigame.
     */
    @Override
    public void removePlayer(@NotNull Player player) {
        super.removePlayer(player);
        if (this.alive.remove(player.getUniqueId())) {
            switch (this.alive.size()) {
                case 0:
                    this.endMinigame(player);
                    break;
                case 1:
                    this.endMinigame(Bukkit.getOfflinePlayer(this.alive.get(0)));
                    break;
                default:
                    Bukkit.broadcast(Component.newline().append(Component.text(ChatColor.GREEN + player.getName() + " has died. There are " + this.alive.size() + " players remaining.").append(Component.newline())));
            }
        }
    }

    public void endMinigame(@Nullable OfflinePlayer player) {
        if (player == null) {
            Bukkit.broadcast(Component.text(ChatColor.RED + "An error occurred while retrieving the winner of the Death Swap. Contact an administrator if this occurs."));
        } else {
            Bukkit.broadcast(Component.text(ChatColor.GREEN + player.getName() + " has won the Death Swap."));
        }

        if (this.swapRunnable != null && !this.swapRunnable.isCancelled()) {
            this.swapRunnable.cancel();
            this.swapRunnable = null;
        }

        super.endMinigame(false);
    }

    @Override
    public void handleEvent(Event event) {
        super.handleEvent(event);
        if (event instanceof PlayerEvent) {
            Player player = ((PlayerEvent) event).getPlayer();
            if (event instanceof PlayerTeleportEvent) {
                PlayerTeleportEvent e = (PlayerTeleportEvent) event;
                switch (e.getCause()) {
                    case NETHER_PORTAL:
                    case END_PORTAL:
                        e.setCancelled(true);
                        player.sendMessage(ChatColor.RED + "The Nether and End has been disabled for Death Swap. You must rely on another way to kill the opponents.");
                        break;
                    default:
                }
            }
        } else if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
            if ((e.getDamager() instanceof Player && e.getEntity() instanceof Player && TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - this.startTimestamp) >= 5) || this.isSpectator(e.getDamager().getUniqueId()) || this.isSpectator(e.getEntity().getUniqueId())) {
                e.setCancelled(true);
            }
        } else if (event instanceof PlayerDeathEvent) {
            PlayerDeathEvent e = (PlayerDeathEvent) event;
            if (!this.status.isInProgress()) {
                return;
            }

            Player player = e.getEntity();
            e.setCancelled(true);
            if (this.alive.remove(player.getUniqueId())) {
                PlayerUtil.setSpectator(player);
                this.hideSpectator(player);
                switch (this.alive.size()) {
                    case 0:
                        this.endMinigame(Bukkit.getOfflinePlayer(player.getUniqueId()));
                        break;
                    case 1:
                        this.endMinigame(Bukkit.getOfflinePlayer(this.alive.get(0)));
                        break;
                    default:
                        Bukkit.broadcast(Component.newline().append(Component.text(ChatColor.GREEN + player.getName() + " has died. There are " + this.alive.size() + " players remaining.").append(Component.newline())));
                }
            }
        }
    }
}
