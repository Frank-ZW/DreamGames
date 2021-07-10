package net.craftgalaxy.lockout.minigame;

import com.google.common.collect.Iterables;
import net.craftgalaxy.lockout.challenge.AbstractChallenge;
import net.craftgalaxy.lockout.challenge.impl.advancement.ChallengeObtainDiamonds;
import net.craftgalaxy.lockout.challenge.impl.advancement.ChallengeObtainWitherSkull;
import net.craftgalaxy.lockout.challenge.impl.advancement.ChallengeSummonWither;
import net.craftgalaxy.lockout.challenge.impl.block.ChallengePlaceBeacon;
import net.craftgalaxy.lockout.challenge.impl.consume.ChallengeConsumeNotchApple;
import net.craftgalaxy.lockout.challenge.impl.consume.ChallengeConsumeSuspiciousStew;
import net.craftgalaxy.lockout.challenge.impl.entity.*;
import net.craftgalaxy.lockout.challenge.impl.interact.*;
import net.craftgalaxy.lockout.challenge.impl.misc.*;
import net.craftgalaxy.lockout.challenge.impl.movement.ChallengeExploreIceSpikedBiome;
import net.craftgalaxy.lockout.challenge.impl.movement.ChallengeReachHeightLimit;
import net.craftgalaxy.lockout.challenge.types.AbstractStructureChallenge;
import net.craftgalaxy.lockout.player.PlayerData;
import net.craftgalaxy.lockout.runnable.PlayerTrackerRunnable;
import net.craftgalaxy.lockout.runnable.StructureRunnable;
import net.craftgalaxy.minigameservice.bukkit.minigame.functional.IInteractiveBoard;
import net.craftgalaxy.minigameservice.bukkit.minigame.functional.IPlayerTracker;
import net.craftgalaxy.minigameservice.bukkit.minigame.impl.InteractiveBoardHandler;
import net.craftgalaxy.minigameservice.bukkit.minigame.impl.PlayerTrackerHandler;
import net.craftgalaxy.minigameservice.bukkit.minigame.types.AbstractSurvivalMinigame;
import net.craftgalaxy.minigameservice.bukkit.util.minecraft.ItemUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class LockOutMinigame extends AbstractSurvivalMinigame implements IPlayerTracker, IInteractiveBoard<AbstractChallenge> {

    private static final LinkedHashMap<ChatColor, Material> GUI_COLORS = new LinkedHashMap<>() {{
        this.put(ChatColor.GREEN, Material.LIME_CONCRETE);
        this.put(ChatColor.BLUE, Material.BLUE_CONCRETE);
        this.put(ChatColor.YELLOW, Material.YELLOW_CONCRETE);
        this.put(ChatColor.GRAY, Material.GRAY_CONCRETE);
    }};

    private final List<AbstractChallenge> challenges = new ArrayList<>(Arrays.asList(
            new ChallengeObtainDiamonds(this),
            new ChallengeObtainWitherSkull(this),
            new ChallengeSummonWither(this),
            new ChallengePlaceBeacon(this),
            new ChallengeConsumeNotchApple(this),
            new ChallengeConsumeSuspiciousStew(this),
            new ChallengeBreedChickens(this),
            new ChallengeBreedHorses(this),
            new ChallengeHatchChicken(this),
            new ChallengeKillSlime(this),
            new ChallengeKillWither(this),
            new ChallengeShearSheep(this),
            new ChallengeTameCat(this),
            new ChallengeFillComposter(this),
            new ChallengeLightFirework(this),
            new ChallengeLootBuriedTreasure(this),
            new ChallengeLootDungeonTreasure(this),
            new ChallengeLootShipwreckTreasure(this),
            new ChallengePlayMusicDisc(this),
            new ChallengeEnchantItem(this),
            new ChallengeEnterBed(this),
            new ChallengeFallDamageDeath(this),
            new ChallengePrimeTNT(this),
            new ChallengeWitherDamage(this),
            new ChallengeExploreIceSpikedBiome(this),
            new ChallengeReachHeightLimit(this)
    ));

    private Map<UUID, PlayerData> teams = new HashMap<>();
    private AbstractChallenge[] uncompleted = new AbstractChallenge[25];
    private IPlayerTracker playerTrackerHandler;
    private IInteractiveBoard<AbstractChallenge> boardHandler;
    private BukkitRunnable trackerRunnable;
    private BukkitRunnable structureRunnable;
    private int completionThreshold;

    public LockOutMinigame(int gameKey, Location lobby) {
        super(gameKey, "Lock Out", lobby);
        this.playerTrackerHandler = new PlayerTrackerHandler(ItemUtil.DEFAULT_PLAYER_TRACKER, "lockout_player_tracker");
        this.boardHandler = new InteractiveBoardHandler<>(Material.NETHER_STAR, ItemUtil.LOCKOUT_BOARD_TITLE, Component.text(ChatColor.GREEN + "Click to view the available challenges"), 5, "lockout_board");
        int i = 0;
        for (int y = 0; y < 5; y++) {
            for (int x = 2; x < 7; x++) {
                int index = x + 9 * y;
                AbstractChallenge challenge = this.challenges.remove(this.random.nextInt(this.challenges.size()));
                ItemStack icon = new ItemStack(Material.RED_CONCRETE);
                ItemMeta iconMeta = icon.getItemMeta();
                if (iconMeta != null) {
                    iconMeta.displayName(Component.text(ChatColor.RED + challenge.getDisplayMessage()));
                    icon.setItemMeta(iconMeta);
                }

                this.uncompleted[i++] = challenge;
                this.setGuiIndex(challenge, index);
                this.setGuiIcon(index, icon);
            }
        }
    }

    @Override
    protected String getStartMessage(@NotNull UUID uniqueId) {
        return ChatColor.GREEN + "Right click the Nether Star to view the available challenges. The player with the most challenges completed wins the Lock Out.";
    }

    @Override
    public boolean isPlayerTracker(@NotNull ItemStack item) {
        return this.playerTrackerHandler.isPlayerTracker(item);
    }

    @Override
    public void updatePlayerTracker(@NotNull Player player, @Nullable Player target, @NotNull ItemStack tracker) {
        this.playerTrackerHandler.updatePlayerTracker(player, target, tracker);
    }

    @Override
    public @NotNull ItemStack createPlayerTracker() {
        return this.playerTrackerHandler.createPlayerTracker();
    }

    @Override
    public void updateBoard() {
        this.boardHandler.updateBoard();
    }

    @Override
    public void setGuiIcon(int index, @NotNull ItemStack item) {
        this.boardHandler.setGuiIcon(index, item);
    }

    @Override
    public void setGuiIndex(AbstractChallenge key, int index) {
        this.boardHandler.setGuiIndex(key, index);
    }

    @Override
    public void openBoardGui(@NotNull Player player) {
        this.boardHandler.openBoardGui(player);
    }

    @Override
    public boolean isBoardIcon(@NotNull ItemStack item) {
        return this.boardHandler.isBoardIcon(item);
    }

    @Override
    public int getGuiIndex(AbstractChallenge key) {
        return this.boardHandler.getGuiIndex(key);
    }

    @Override
    public @NotNull ItemStack createBoardGui() {
        return this.boardHandler.createBoardGui();
    }

    @Override
    public @Nullable ItemStack getGuiIcon(int index) {
        return this.boardHandler.getGuiIcon(index);
    }

    @Override
    protected boolean onPlayerStartTeleport(@NotNull Player player, @NotNull Location to) {
        player.getInventory().setItem(0, this.createPlayerTracker());
        player.getInventory().setItem(8, this.createBoardGui());
        return super.onPlayerStartTeleport(player, to);
    }

    @Override
    public void removePlayer(@NotNull Player player) {
        super.removePlayer(player);
        if (this.teams.remove(player.getUniqueId()) != null && this.status.isInProgress()) {
            switch (this.teams.size()) {
                case 0:
                    this.endMinigame(null, true);
                    break;
                case 1:
                    PlayerData playerData = Iterables.getOnlyElement(this.teams.values(), null);
                    this.endMinigame(playerData, playerData == null);
                    break;
                default:
            }
        }
    }

    @Override
    public Component getGameDisplayName(@NotNull OfflinePlayer player) {
        String prefix = null;
        Chat chat = this.plugin.getChat();
        if (chat != null) {
            prefix = chat.getPlayerPrefix(player.getPlayer());
        }

        PlayerData playerData = this.teams.get(player.getUniqueId());
        return Component.text((prefix == null ? "" : ChatColor.translateAlternateColorCodes('&', prefix) + ChatColor.RESET + " ") + (playerData == null ? ChatColor.RED : playerData.getChatColor()) + player.getName());
    }

    @Override
    public void startCountdown(@NotNull List<UUID> players) {
        super.startCountdown(players);
        this.completionThreshold = (int) Math.ceil(25.0D / players.size());
        Set<Map.Entry<ChatColor, Material>> entries = LockOutMinigame.GUI_COLORS.entrySet();
        int index = 0;
        for (UUID uniqueId : players) {
            OfflinePlayer offline = Bukkit.getOfflinePlayer(uniqueId);
            Map.Entry<ChatColor, Material> entry = Iterables.get(entries, index++);
            this.teams.put(uniqueId, new PlayerData(offline.getName(), entry.getKey(), entry.getValue()));
        }

        this.trackerRunnable = new PlayerTrackerRunnable(this);
        this.trackerRunnable.runTaskTimer(this.plugin, 60, 40);

        this.structureRunnable = new StructureRunnable(this);
        this.structureRunnable.runTaskTimer(this.plugin, 40, 20);
    }

    @Override
    public void cancelCountdown() {
        super.cancelCountdown();
        this.trackerRunnable.cancel();
        this.completionThreshold = 0;
        this.teams.clear();
    }

    @Override
    public void unload() {
        super.unload();
        this.teams.clear();
        this.uncompleted = null;
        this.trackerRunnable = null;
        this.playerTrackerHandler = null;
        this.boardHandler = null;
        this.teams = null;
    }

    @Override
    public void handleEvent(Event event) {
        super.handleEvent(event);
        if (event instanceof PlayerEvent) {
            Player player = ((PlayerEvent) event).getPlayer();
            if (event instanceof PlayerRespawnEvent) {
                PlayerRespawnEvent e = (PlayerRespawnEvent) event;
                player.getInventory().setItem(0, this.createPlayerTracker());
                player.getInventory().setItem(8, this.createBoardGui());
                e.setRespawnLocation(player.getBedSpawnLocation() == null ? this.getOverworld().getSpawnLocation() : player.getBedSpawnLocation());
            } else if (event instanceof PlayerInteractEvent) {
                PlayerInteractEvent e = (PlayerInteractEvent) event;
                ItemStack item = e.getItem();
                if ((e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) && item != null) {
                    if (this.isBoardIcon(item)) {
                        this.openBoardGui(player);
                    } else if (this.isPlayerTracker(item)) {
                        List<Player> online = this.players.stream().map(Bukkit::getPlayer).filter(p -> p != null && !p.getUniqueId().equals(player.getUniqueId()) && !this.isSpectator(p.getUniqueId())).collect(Collectors.toList());
                        switch (online.size()) {
                            case 0:
                                break;
                            case 1:
                                this.teams.computeIfPresent(player.getUniqueId(), (k, v) -> {
                                    v.setTracking(online.get(0).getUniqueId());
                                    return v;
                                });

                                break;
                            default:
                                Inventory players = Bukkit.createInventory(player, 27, ItemUtil.LOCKOUT_PLAYER_TRACKER);
                                for (Player target : online) {
                                    ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                                    SkullMeta meta = (SkullMeta) head.getItemMeta();
                                    if (meta != null) {
                                        meta.displayName(Component.text(ChatColor.GREEN + target.getName()));
                                        meta.setOwningPlayer(Bukkit.getOfflinePlayer(target.getUniqueId()));
                                        head.setItemMeta(meta);
                                    }

                                    players.addItem(head);
                                }

                                player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                                player.openInventory(players);
                        }
                    }
                }
            } else if (event instanceof PlayerDropItemEvent) {
                PlayerDropItemEvent e = (PlayerDropItemEvent) event;
                ItemStack item = e.getItemDrop().getItemStack();
                if (this.isBoardIcon(item) || this.isPlayerTracker(item)) {
                    e.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You cannot drop this item.");
                }
            }
        } else if (event instanceof PlayerDeathEvent) {
            PlayerDeathEvent e = (PlayerDeathEvent) event;
            e.getDrops().removeIf(item -> this.isBoardIcon(item) || this.isPlayerTracker(item));
        } else if (event instanceof InventoryClickEvent) {
            InventoryClickEvent e = (InventoryClickEvent) event;
            if (ItemUtil.LOCKOUT_BOARD_TITLE.equals(e.getView().title())) {
                e.setCancelled(true);
                return;
            }

            ItemStack clicked = e.getCurrentItem();
            if (clicked != null && ItemUtil.LOCKOUT_PLAYER_TRACKER.equals(e.getView().title())) {
                e.setCancelled(true);
                Player player = (Player) e.getWhoClicked();
                ItemStack compass = player.getInventory().getItemInMainHand();
                if (compass.getType().isAir()) {
                    compass = player.getInventory().getItemInOffHand();
                }

                if (this.isPlayerTracker(compass)) {
                    Component displayName = clicked.getItemMeta().displayName();
                    if (displayName == null) {
                        player.sendMessage(ChatColor.RED + "A player with that name could not be found! Contact an administrator if this occurs.");
                        return;
                    }

                    Player target = Bukkit.getPlayer(ChatColor.stripColor(PlainComponentSerializer.plain().serialize(displayName)));
                    if (target == null) {
                        player.sendMessage(ChatColor.RED + "That player is not online.");
                        return;
                    }

                    this.teams.computeIfPresent(player.getUniqueId(), (k, v) -> {
                        v.setTracking(target.getUniqueId());
                        return v;
                    });

                    player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                } else {
                    player.sendMessage(ChatColor.RED + "You must be holding the Player Tracker to track a specific player.");
                }
            }
        }

        this.handleChallenge(event);
    }

    public boolean handleStructureChallenge(Player player) {
        int previous = -1;
        for (int i = 0; i < this.uncompleted.length; i++) {
            AbstractChallenge challenge = this.uncompleted[i];
            if (challenge instanceof AbstractStructureChallenge) {
                if (challenge.handle(player)) {
                    this.challenges.add(challenge);
                    this.uncompleted[i] = null;
                } else {
                    previous = i;
                }
            }
        }

        return previous < 0;
    }

    public void handleChallenge(Object object) {
        for (int i = 0; i < this.uncompleted.length; i++) {
            AbstractChallenge challenge = this.uncompleted[i];
            if (challenge != null && challenge.handle(object)) {
                this.challenges.add(challenge);
                this.uncompleted[i] = null;
            }
        }
    }

    public void completeChallenge(@NotNull Player player, AbstractChallenge challenge) {
        PlayerData playerData = this.teams.get(player.getUniqueId());
        if (playerData == null || challenge.isCompleted()) {
            return;
        }

        challenge.setCompleted(true);
        int index = this.getGuiIndex(challenge);
        ItemStack item = this.getGuiIcon(index);
        if (item != null) {
            item.setType(playerData.getIcon());
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta != null) {
                itemMeta.displayName(Component.text(ChatColor.GREEN + challenge.getDisplayMessage()));
                itemMeta.lore(Collections.singletonList(Component.text(playerData.getChatColor() + "Completed by " + player.getName())));
                item.setItemMeta(itemMeta);
            }

            this.setGuiIcon(index, item);
            this.updateBoard();
            this.teams.computeIfPresent(player.getUniqueId(), (k, v) -> v.incrementCompleted());
        }

        Bukkit.broadcast(Component.newline().append(Component.text(playerData.getChatColor() + player.getName() + ChatColor.RESET + ChatColor.WHITE + " has completed the challenge: " + ChatColor.GREEN + ChatColor.BOLD + challenge.getDisplayMessage())).append(Component.newline()));
        int remaining = 0;
        for (AbstractChallenge c : this.uncompleted) {
            if (c != null) {
                ++remaining;
            }
        }

        if (playerData.getCompleted() >= this.completionThreshold || remaining <= 1) {
            this.endMinigame(playerData, false);
        }
    }

    public void endMinigame(@Nullable PlayerData playerData, boolean urgently) {
        if (playerData != null) {
            Bukkit.broadcast(Component.newline().append(Component.text(playerData.getChatColor() + playerData.getName() + ChatColor.WHITE + " won the " + ChatColor.GREEN + this.getName() + ChatColor.WHITE + " with " + ChatColor.GREEN + playerData.getCompleted() + " challenges " + ChatColor.WHITE + "completed!")).append(Component.newline()));
        }

        super.endMinigame(urgently);
    }

    public Set<UUID> getTeamsUUID() {
        return this.teams.keySet();
    }

    public Set<Map.Entry<UUID, PlayerData>> getTeamsEntry() {
        return this.teams.entrySet();
    }

    @Override
    public int hashCode() {
        return 47 * this.gameKey;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof LockOutMinigame)) {
            return false;
        }

        return this.gameKey == ((LockOutMinigame) obj).getGameKey();
    }
}
