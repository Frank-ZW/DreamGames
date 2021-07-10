package net.craftgalaxy.lockout.challenge.impl.interact;

import net.craftgalaxy.lockout.challenge.functional.IBlockChallenge;
import net.craftgalaxy.lockout.challenge.functional.IMultiItemChallenge;
import net.craftgalaxy.lockout.challenge.types.AbstractInteractChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ChallengePlayMusicDisc extends AbstractInteractChallenge implements IBlockChallenge, IMultiItemChallenge {

    public ChallengePlayMusicDisc(LockOutMinigame lockOut) {
        super(lockOut);
    }

    @Override
    public @NotNull String getDisplayMessage() {
        return "Play a music disc!";
    }

    @Override
    public boolean onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = e.getClickedBlock();
            ItemStack item = e.getItem();
            if (block != null && item != null && block.getType() == this.getMaterial() && this.getItemTypes().contains(item.getType())) {
                this.lockOut.completeChallenge(e.getPlayer(), this);
                return true;
            }
        }

        return false;
    }

    @Override
    public @NotNull Material getMaterial() {
        return Material.JUKEBOX;
    }

    @Override
    public @NotNull Set<Material> getItemTypes() {
        return Set.of(Material.MUSIC_DISC_11,
                Material.MUSIC_DISC_13,
                Material.MUSIC_DISC_BLOCKS,
                Material.MUSIC_DISC_CAT,
                Material.MUSIC_DISC_MALL,
                Material.MUSIC_DISC_CHIRP,
                Material.MUSIC_DISC_FAR,
                Material.MUSIC_DISC_MELLOHI,
                Material.MUSIC_DISC_PIGSTEP,
                Material.MUSIC_DISC_STAL,
                Material.MUSIC_DISC_STRAD,
                Material.MUSIC_DISC_WAIT,
                Material.MUSIC_DISC_WARD
        );
    }
}
