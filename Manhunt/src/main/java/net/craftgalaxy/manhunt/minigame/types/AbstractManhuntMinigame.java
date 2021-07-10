package net.craftgalaxy.manhunt.minigame.types;

import net.craftgalaxy.minigameservice.bukkit.minigame.impl.PlayerTrackerHandler;
import net.craftgalaxy.minigameservice.bukkit.minigame.types.AbstractSurvivalMinigame;
import net.craftgalaxy.minigameservice.bukkit.minigame.functional.IPlayerTracker;
import net.craftgalaxy.minigameservice.bukkit.util.minecraft.ItemUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractManhuntMinigame extends AbstractSurvivalMinigame implements IPlayerTracker {

    protected UUID speedrunner;
    protected final Set<UUID> hunters = new HashSet<>();
    protected boolean bedBombing;
    private final IPlayerTracker playerTrackerHandler;

    public AbstractManhuntMinigame(int gameKey, Location lobby) {
        this(gameKey, "Manhunt", lobby);
    }

    public AbstractManhuntMinigame(int gameKey, String name, Location lobby) {
        super(gameKey, name, lobby);
        this.playerTrackerHandler = new PlayerTrackerHandler(ItemUtil.DEFAULT_PLAYER_TRACKER, "manhunt_player_tracker");
        this.bedBombing = false;
    }

    /**
     * Updates the location the player tracker should point to. The player tracker will update
     * to the speedrunner's latest location only when it is interacted with.
     * <p>
     * The item passed must be a compass.
     * @param hunter    The hunter right clicking the compass.
     * @param target    The target the player tracker should point to.
     * @param compass   The compass being clicked.
     */
    @Override
    public void updatePlayerTracker(@NotNull Player hunter, @Nullable Player target, @NotNull ItemStack compass) {
        this.playerTrackerHandler.updatePlayerTracker(hunter, target, compass);
    }

    @Override
    public boolean isPlayerTracker(@NotNull ItemStack item) {
        return this.playerTrackerHandler.isPlayerTracker(item);
    }

    @Override
    public @NotNull ItemStack createPlayerTracker() {
        return this.playerTrackerHandler.createPlayerTracker();
    }

    public boolean isSpeedrunner(@NotNull UUID uniqueId) {
        return uniqueId.equals(this.speedrunner);
    }

    public boolean isHunter(UUID uniqueId) {
        return this.hunters.contains(uniqueId);
    }

    @Nullable
    public Player getSpeedrunnerPlayer() {
        return Bukkit.getPlayer(this.speedrunner);
    }

    @Override
    public Component getGameDisplayName(@NotNull OfflinePlayer player) {
        return Component.text((this.isSpeedrunner(player.getUniqueId()) ? ChatColor.GREEN : ChatColor.RED) + player.getName());
    }

    /**
     * Removes the player from the minigame.
     *
     * @param player The player being removed from the minigame.
     */
    @Override
    public void removePlayer(@NotNull Player player) {
        super.removePlayer(player);
        if (this.isSpeedrunner(player.getUniqueId())) {
            this.endMinigame(false, false);
            return;
        }

        if (this.hunters.remove(player.getUniqueId()) && this.hunters.isEmpty()) {
            this.endMinigame(true, false);
        }
    }

    @Override
    protected boolean onPlayerStartTeleport(@NotNull Player player, @NotNull Location to) {
        if (this.isSpeedrunner(player.getUniqueId())) {
            to = this.getOverworld().getSpawnLocation();
            return !super.onPlayerStartTeleport(player, to);
        } else {
            player.getInventory().setItem(8, this.createPlayerTracker());
            return super.onPlayerStartTeleport(player, to);
        }
    }

    @Override
    public void startTeleport() {
        Player player = this.getSpeedrunnerPlayer();
        if (player == null) {
            Bukkit.broadcast(Component.text( ChatColor.RED + "An error occurred while selecting the speedrunner. Contact an administrator if this occurs."));
            this.endMinigame(true);
            return;
        }

        super.startTeleport();
    }

    /**
     * Returns the message sent to the players at the start of the game after the player has been teleported.
     *
     * @param uniqueId The UUID of the player to be sent the message.
     * @return The message that should be sent to the player at the start of the match describing their role.
     */
    @Override
    protected String getStartMessage(@NotNull UUID uniqueId) {
        return this.isSpeedrunner(uniqueId) ? ChatColor.GREEN + "You are the speedrunner. You must kill the Enderdragon before the hunters kill you." : ChatColor.RED + "You are " + (this.hunters.size() == 1 ? "the" : "a") + " hunter. You must use your Player Tracker to relentlessly hunt and kill the speedrunner.";
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
        this.speedrunner = players.remove(this.random.nextInt(players.size()));
        this.hunters.addAll(players);
    }

    @Override
    public void cancelCountdown() {
        super.cancelCountdown();
        this.speedrunner = null;
        this.hunters.clear();
    }

    @Override
    public void unload() {
        super.unload();
        this.speedrunner = null;
        this.hunters.clear();
    }

    public void endMinigame(boolean isSpeedrunnerWinner, boolean urgently) {
        if (!this.status.isInProgress()) {
            return;
        }

        if (isSpeedrunnerWinner) {
            Bukkit.broadcast(Component.text(ChatColor.GREEN + "The speedrunner has won the Manhunt"));
        } else {
            Bukkit.broadcast(Component.text(ChatColor.GREEN + "The hunter" + (this.hunters.size() == 1 ? " has" : "s have") + " won the Manhunt."));
        }

        super.endMinigame(urgently);
    }
}
