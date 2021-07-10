package net.craftgalaxy.lockout.challenge.impl.misc;

import net.craftgalaxy.lockout.challenge.AbstractChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.jetbrains.annotations.NotNull;

public class ChallengeEnchantItem extends AbstractChallenge {

    public ChallengeEnchantItem(LockOutMinigame lockOut) {
        super(lockOut);
    }

    @Override
    public @NotNull String getDisplayMessage() {
        return "Enchant an item!";
    }

    @Override
    public boolean handle(Object object) {
        if (object instanceof EnchantItemEvent) {
            EnchantItemEvent e = (EnchantItemEvent) object;
            this.lockOut.completeChallenge(e.getEnchanter(), this);
            return true;
        }

        return false;
    }
}
