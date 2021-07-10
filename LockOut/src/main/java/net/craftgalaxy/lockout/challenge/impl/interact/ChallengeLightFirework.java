package net.craftgalaxy.lockout.challenge.impl.interact;

import net.craftgalaxy.lockout.challenge.functional.IItemChallenge;
import net.craftgalaxy.lockout.challenge.types.AbstractInteractChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ChallengeLightFirework extends AbstractInteractChallenge implements IItemChallenge {

    public ChallengeLightFirework(LockOutMinigame lockOut) {
        super(lockOut);
    }

    @Override
    public @NotNull String getDisplayMessage() {
        return "Light a firework!";
    }

    @Override
    public boolean onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = e.getItem();
            if (item != null && item.getType() == this.getItemType()) {
                this.lockOut.completeChallenge(e.getPlayer(), this);
                return true;
            }
        }

        return false;
    }

    @Override
    public @NotNull Material getItemType() {
        return Material.FIREWORK_ROCKET;
    }
}
