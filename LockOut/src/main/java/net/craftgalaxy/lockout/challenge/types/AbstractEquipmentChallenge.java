package net.craftgalaxy.lockout.challenge.types;

import net.craftgalaxy.lockout.challenge.AbstractChallenge;
import net.craftgalaxy.lockout.challenge.functional.IMultiItemChallenge;
import net.craftgalaxy.lockout.minigame.LockOutMinigame;

public abstract class AbstractEquipmentChallenge extends AbstractChallenge implements IMultiItemChallenge {

    public AbstractEquipmentChallenge(LockOutMinigame lockOut) {
        super(lockOut);
    }
}
