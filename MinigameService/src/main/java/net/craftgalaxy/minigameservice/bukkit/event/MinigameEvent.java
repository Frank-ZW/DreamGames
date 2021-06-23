package net.craftgalaxy.minigameservice.bukkit.event;

import net.craftgalaxy.minigameservice.bukkit.minigame.AbstractMinigame;
import org.bukkit.event.Event;

public abstract class MinigameEvent extends Event {

    protected AbstractMinigame minigame;

    public MinigameEvent(AbstractMinigame minigame) {
        this.minigame = minigame;
    }

    public AbstractMinigame getMinigame() {
        return this.minigame;
    }
}
