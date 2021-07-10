package net.craftgalaxy.lockout.challenge.impl.interact;

import net.craftgalaxy.lockout.challenge.types.AbstractInteractChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.loot.LootTables;
import org.jetbrains.annotations.NotNull;

public class ChallengeLootBuriedTreasure extends AbstractInteractChallenge {

    public ChallengeLootBuriedTreasure(LockOutMinigame lockOut) {
        super(lockOut);
    }

    @Override
    public @NotNull String getDisplayMessage() {
        return "Loot a buried treasure chest!";
    }

    @Override
    public boolean onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = e.getClickedBlock();
            if (block != null && block.getState() instanceof Chest) {
                Chest chest = (Chest) block.getState();
                if (LootTables.BURIED_TREASURE.getLootTable().equals(chest.getLootTable())) {
                    this.lockOut.completeChallenge(e.getPlayer(), this);
                    return true;
                }
            }
        }

        return false;
    }
}
