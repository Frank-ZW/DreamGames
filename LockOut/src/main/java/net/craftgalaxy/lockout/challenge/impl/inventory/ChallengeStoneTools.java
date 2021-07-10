package net.craftgalaxy.lockout.challenge.impl.inventory;

import net.craftgalaxy.lockout.challenge.types.AbstractEquipmentChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ChallengeStoneTools extends AbstractEquipmentChallenge {

    private final Set<Material> items = this.getItemTypes();

    public ChallengeStoneTools(LockOutMinigame lockOut) {
        super(lockOut);
    }

    @Override
    public @NotNull String getDisplayMessage() {
        return "Full set of stone tools!";
    }

    @Override
    public boolean handle(Object object) {
        if (object instanceof PlayerAttemptPickupItemEvent) {
            PlayerAttemptPickupItemEvent e = (PlayerAttemptPickupItemEvent) object;
            if (this.items.contains(e.getItem().getItemStack().getType())) {
                Player player = e.getPlayer();
                for (ItemStack item : player.getInventory().getContents()) {
                    
                }
            }
        }

        return false;
    }

    @Override
    public @NotNull Set<Material> getItemTypes() {
        return Set.of(Material.STONE_AXE, Material.STONE_PICKAXE, Material.STONE_SWORD, Material.STONE_HOE, Material.STONE_SHOVEL);
    }
}
