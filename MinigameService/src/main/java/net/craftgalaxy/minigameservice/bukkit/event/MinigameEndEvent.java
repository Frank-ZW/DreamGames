package net.craftgalaxy.minigameservice.bukkit.event;

import net.craftgalaxy.minigameservice.bukkit.minigame.types.AbstractMinigame;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MinigameEndEvent extends MinigameEvent {

    private static final HandlerList handlers = new HandlerList();

    public MinigameEndEvent(AbstractMinigame minigame) {
        super(minigame);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
