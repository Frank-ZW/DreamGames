package net.craftgalaxy.manhunt.minigame.types;

import net.craftgalaxy.minigameservice.bukkit.minigame.AbstractSurvivalMinigame;
import net.craftgalaxy.minigameservice.bukkit.util.ItemUtil;
import net.craftgalaxy.minigameservice.bukkit.util.ColorUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractManhuntMinigame extends AbstractSurvivalMinigame {

    protected UUID speedrunner;
    protected final Set<UUID> hunters = new HashSet<>();
    protected boolean bedBombing;

    public AbstractManhuntMinigame(int gameKey, Location lobby) {
        super(gameKey, "Manhunt", lobby);
        this.bedBombing = false;
    }

    /**
     * Updates the location the player tracker should point to. The player tracker will update
     * to the speedrunner's latest location only when it is interacted with.
     * <p>
     * The item passed must be a compass.
     *
     * @param hunter    The hunter right clicking the compass.
     * @param compass   The compass being clicked.
     */
    public void updatePlayerTracker(@NotNull Player hunter, @NotNull ItemStack compass) {
        Player player = this.getSpeedrunnerPlayer();
        if (player == null) {
            hunter.sendActionBar(Component.text("There are no players to track!", TextColor.color(ColorUtil.BUKKIT_RED_CODE)));
            return;
        }

        CompassMeta compassMeta = (CompassMeta) compass.getItemMeta();
        if (hunter.getWorld().equals(player.getWorld())) {
            compassMeta.setLodestone(player.getLocation());
            compassMeta.setLodestoneTracked(false);
            compass.setItemMeta(compassMeta);
            hunter.sendActionBar(Component.text("Currently tracking " + player.getName() + "'s latest location.", TextColor.color(ColorUtil.BUKKIT_GREEN_CODE)));
        } else {
            hunter.sendActionBar(Component.text("There are no players to track!", TextColor.color(ColorUtil.BUKKIT_RED_CODE)));
        }
    }

    public boolean isPlayerTracker(@Nullable ItemStack item) {
        if (item == null) {
            return false;
        }

        net.minecraft.server.v1_16_R3.ItemStack nms = CraftItemStack.asNMSCopy(item);
        NBTTagCompound compound = nms.getTag();
        return compound != null && compound.getBoolean("player_tracker") && item.getItemMeta() instanceof CompassMeta;
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
    public String getGameDisplayName(@NotNull OfflinePlayer player) {
        return (this.isSpeedrunner(player.getUniqueId()) ? ChatColor.GREEN : ChatColor.RED) + player.getName();
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
            player.getInventory().setItem(8, ItemUtil.createPlayerTracker(ItemUtil.MANHUNT_PLAYER_TRACKER));
            return super.onPlayerStartTeleport(player, to);
        }
    }

    @Override
    public void startTeleport() {
        Player player = this.getSpeedrunnerPlayer();
        if (player == null) {
            Bukkit.broadcast(Component.text("An error occurred while selecting the speedrunner. Contact an administrator if this occurs.", TextColor.color(ColorUtil.BUKKIT_RED_CODE)));
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
            Bukkit.broadcast(Component.text("The speedrunner has won the Manhunt", TextColor.color(ColorUtil.BUKKIT_GREEN_CODE)));
        } else {
            Bukkit.broadcast(Component.text("The hunter" + (this.hunters.size() == 1 ? " has" : "s have") + " won the Manhunt.", TextColor.color(ColorUtil.BUKKIT_GREEN_CODE)));
        }

        super.endMinigame(urgently);
    }
}
