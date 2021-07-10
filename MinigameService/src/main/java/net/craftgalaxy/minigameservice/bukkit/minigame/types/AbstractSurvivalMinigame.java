package net.craftgalaxy.minigameservice.bukkit.minigame;

import net.craftgalaxy.minigameservice.bukkit.util.minecraft.PlayerUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

public abstract class AbstractSurvivalMinigame extends AbstractMinigame {

    protected Map<World.Environment, World> worlds = new HashMap<>();
    protected Map<UUID, Set<Advancement>> advancements = new HashMap<>();
    protected WorldType worldType;
    protected long seed;
    protected boolean allowNether;
    protected boolean allowEnd;

    public AbstractSurvivalMinigame(int gameKey, String name, Location lobby) {
        this(gameKey, name, lobby, true, true);
    }

    public AbstractSurvivalMinigame(int gameKey, String name, Location lobby, boolean allowNether, boolean allowEnd) {
        super(gameKey, name, lobby);
        this.worldType = WorldType.NORMAL;
        this.seed = this.random.nextLong();
        this.allowNether = allowNether;
        this.allowEnd = allowEnd;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    /**
     * Logs advancements players have made during the minigame. The server uses the list to
     * revoke the awarded advancements at the end of the minigame. This is much faster than
     * looping through every available advancement.
     *
     * @param player        The player the advancement was awarded to.
     * @param advancement   The advancement to be awarded.
     */
    public void addAwardedAdvancement(@NotNull Player player, @NotNull Advancement advancement) {
        this.advancements.computeIfAbsent(player.getUniqueId(), ignored -> new HashSet<>()).add(advancement);
    }

    /**
     * Removes all awarded advancements during the game. This is much faster than
     * looping through every available advancement and revoking each one individually.
     *
     * @param player    The player to revoke all advancements.
     */
    public void clearAwardedAdvancements(@NotNull Player player) {
        Set<Advancement> advancements = this.advancements.remove(player.getUniqueId());
        if (advancements != null) {
            PlayerUtil.clearAdvancements(player);
        }
    }

    @Override
    public void deleteWorlds() {
        for (World world : this.worlds.values()) {
            if (world.getPlayerCount() != 0) {
                List<Player> online = world.getPlayers();
                for (Player player : online) {
                    player.teleport(this.lobby);
                    player.sendMessage(ChatColor.RED + "You were unexpectedly in the world " + world.getName() + " as it was being deleted. You have been teleported back to the lobby.");
                }
            }

            Bukkit.unloadWorld(world, false);
            try {
                FileUtils.deleteDirectory(world.getWorldFolder());
            } catch (IOException e) {
                e.printStackTrace();
            }
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
    @Override
    protected boolean onPlayerStartTeleport(@NotNull Player player, @NotNull Location to) {
        player.teleportAsync(to).thenAccept(result -> {
            if (result) {
                player.sendMessage(this.getStartMessage(player.getUniqueId()));
            } else {
                player.sendMessage(ChatColor.RED + "Failed to teleport you to the " + this.getName() + " world(s). Contact an administrator if this occurs.");
            }
        });

        return true;
    }

    /**
     * Handles player actions for player teleportation at the end of the match.
     *
     * @param player    The player being teleported at the end of the match.
     */
    @Override
    protected void onPlayerEndTeleport(@NotNull Player player) {
        if (this.isSpectator(player.getUniqueId())) {
            PlayerUtil.unsetSpectator(player);
            this.showSpectator(player);
        } else {
            this.clearAwardedAdvancements(player);
            PlayerUtil.resetAttributes(player);
        }

        player.teleport(this.lobby);
    }

    @Override
    public void startTeleport() {
        this.status = MinigameStatus.IN_PROGRESS;
        World overworld = this.getOverworld();
        overworld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        int radius = Math.max(8, 3 * this.players.size());
        float theta = -90.0F;
        float delta = 360.0F / this.players.size();
        for (UUID uniqueId : this.players) {
            Player player = Bukkit.getPlayer(uniqueId);
            if (player == null || !player.isOnline()) {
                continue;
            }

            if (player.isDead()) {
                player.spigot().respawn();
            }

            PlayerUtil.clearAdvancements(player);
            PlayerUtil.resetAttributes(player);

            int x = (int) (overworld.getSpawnLocation().getX() + radius * Math.cos(Math.toRadians(theta)));
            int z = (int) (overworld.getSpawnLocation().getZ() + radius * Math.sin(Math.toRadians(theta)));
            int y = overworld.getHighestBlockYAt(x, z) + 1;
            Location location = new Location(overworld, x, y, z);
            if (this.onPlayerStartTeleport(player, location)) {
                theta += delta;
            }

            this.startTimestamp = System.currentTimeMillis();
        }
    }

    @Override
    public void endTeleport() {
        for (UUID uniqueId : this.players) {
            Player player = Bukkit.getPlayer(uniqueId);
            if (player == null || !player.isOnline()) {
                continue;
            }

            if (player.isDead()) {
                player.spigot().respawn();
            }

            this.onPlayerEndTeleport(player);
        }

        this.status = MinigameStatus.WAITING;
    }

    @Override
    public void cancelCountdown() {
        super.cancelCountdown();
        this.advancements.clear();
    }

    @Override
    public boolean createWorlds() {
        if (this.worlds.size() == 3) {
            return true;
        }

        for (int i = 0; i < 3; i++) {
            World.Environment environment = World.Environment.values()[i];
            World world = new WorldCreator(this.getWorldName(environment)).environment(environment).type(this.worldType).seed(this.seed).createWorld();
            if (world != null) {
                world.setAutoSave(false);
                world.setKeepSpawnInMemory(false);
                world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                this.worlds.put(environment, world);
            }
        }

        return this.worlds.size() == 3;
    }

    @Override
    public void handleEvent(Event event) {
        if (event instanceof PlayerPortalEvent) {
            PlayerPortalEvent e = (PlayerPortalEvent) event;
            Player player = e.getPlayer();
            World fromWorld = e.getFrom().getWorld();
            if (e.getTo().getWorld() == null || e.getFrom().getWorld() == null || e.isCancelled()) {
                return;
            }

            switch (e.getCause()) {
                case NETHER_PORTAL:
                    switch (fromWorld.getEnvironment()) {
                        case NORMAL:
                            e.getTo().setWorld(this.getNether());
                            if (this.players.contains(player.getUniqueId()) && !this.spectators.contains(player.getUniqueId())) {
                                this.plugin.grantNetherAdvancement(player);
                            }

                            break;
                        case NETHER:
                            e.setTo(new Location(this.getOverworld(), e.getFrom().getX() * 8.0D, e.getFrom().getY(), e.getFrom().getZ() * 8.0D));
                            break;
                        default:
                    }

                    break;
                case END_PORTAL:
                    switch (fromWorld.getEnvironment()) {
                        case NORMAL:
                            e.getTo().setWorld(this.getEnd());
                            if (this.players.contains(player.getUniqueId()) && !this.spectators.contains(player.getUniqueId())) {
                                this.plugin.grantEndAdvancement(player);
                            }

                            break;
                        case THE_END:
                            e.setTo(player.getBedSpawnLocation() == null ? this.getOverworld().getSpawnLocation() : player.getBedSpawnLocation());
                            break;
                        default:
                    }

                    break;
                default:
            }
        } else if (event instanceof PortalCreateEvent) {
            PortalCreateEvent e = (PortalCreateEvent) event;
            switch (e.getReason()) {
                case FIRE:
                case NETHER_PAIR:
                    if (!this.allowNether) {
                        e.setCancelled(true);
                    }

                    break;
                default:
                    if (!this.allowEnd) {
                        e.setCancelled(true);
                    }
            }
        } else if (event instanceof PlayerRespawnEvent) {
            PlayerRespawnEvent e = (PlayerRespawnEvent) event;
            if (this.status.isWaiting()) {
                return;
            }

            if (this.getOverworld() == null) {
                Bukkit.broadcast(Component.text(ChatColor.RED + "The " + this.getName() + " overworld failed to generate properly... Contact an administrator if this occurs."));
                this.endMinigame(true);
                return;
            }

            Player player = e.getPlayer();
            e.setRespawnLocation(player.getBedSpawnLocation() == null ? this.getOverworld().getSpawnLocation() : player.getBedSpawnLocation());
        }
    }

    @Override
    public boolean worldsLoaded() {
        return !this.worlds.isEmpty();
    }

    public World getWorld(World.Environment environment) {
        return this.worlds.get(environment);
    }

    public World getOverworld() {
        return this.getWorld(World.Environment.NORMAL);
    }

    public World getNether() {
        return this.getWorld(World.Environment.NETHER);
    }

    public World getEnd() {
        return this.getWorld(World.Environment.THE_END);
    }

    @Override
    public void unload() {
        super.unload();
        this.worlds.clear();
        this.advancements.clear();
    }
}
