package net.craftgalaxy.lockout.challenge.types;

import net.craftgalaxy.lockout.challenge.AbstractChallenge;
import net.craftgalaxy.lockout.challenge.functional.IBlockChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import org.bukkit.event.block.BlockPlaceEvent;

public abstract class AbstractBlockPlaceChallenge extends AbstractChallenge implements IBlockChallenge {

    public AbstractBlockPlaceChallenge(LockOutMinigame lockOut) {
        super(lockOut);
    }

    @Override
    public boolean handle(Object object) {
        if (object instanceof BlockPlaceEvent) {
            BlockPlaceEvent e = (BlockPlaceEvent) object;
            if (e.getBlock().getType() == this.getMaterial()) {
                this.lockOut.completeChallenge(e.getPlayer(), this);
                return true;
            }
        }

        return false;
    }
}
