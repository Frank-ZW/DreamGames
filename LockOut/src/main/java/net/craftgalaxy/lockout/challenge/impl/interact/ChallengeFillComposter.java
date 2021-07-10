package net.craftgalaxy.lockout.challenge.impl.interact;

import net.craftgalaxy.lockout.challenge.types.AbstractInteractChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public class ChallengeFillComposter extends AbstractInteractChallenge {

    public ChallengeFillComposter(LockOutMinigame lockOut) {
        super(lockOut);
    }

    @Override
    public @NotNull String getDisplayMessage() {
        return "Fill a composter!";
    }

    @Override
    public boolean onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = e.getClickedBlock();
            if (block != null && block.getBlockData() instanceof Levelled) {
                Levelled composter = (Levelled) block.getBlockData();
                if (composter.getLevel() >= composter.getMaximumLevel()) {
                    this.lockOut.completeChallenge(e.getPlayer(), this);
                    return true;
                }
            }
        }

        return false;
    }
}
