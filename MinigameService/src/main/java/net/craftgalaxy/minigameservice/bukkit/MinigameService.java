package net.craftgalaxy.minigameservice.bukkit;

import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;

public final class MinigameService extends JavaPlugin {

    private Advancement netherAdvancement;
    private Advancement endAdvancement;
    private Chat chat;

    private static MinigameService instance;

    @Override
    public void onEnable() {
        instance = this;
        Iterator<Advancement> iterator = Bukkit.advancementIterator();
        while (iterator.hasNext()) {
            Advancement advancement = iterator.next();
            if (advancement.getKey().getKey().equals("story/enter_the_nether")) {
                this.netherAdvancement = advancement;
            }

            if (advancement.getKey().getKey().equals("story/enter_the_end")) {
                this.endAdvancement = advancement;
            }

            if (this.netherAdvancement != null && this.endAdvancement != null) {
                break;
            }
        }

        RegisteredServiceProvider<Chat> provider = Bukkit.getServicesManager().getRegistration(Chat.class);
        if (provider != null) {
            this.chat = provider.getProvider();
        }
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        instance = null;
    }

    public void grantAdvancement(@NotNull Player player, @NotNull Advancement advancement) {
        AdvancementProgress progress = player.getAdvancementProgress(advancement);
        Collection<String> remaining = progress.getRemainingCriteria();
        for (String s : remaining) {
            progress.awardCriteria(s);
        }
    }

    public void grantNetherAdvancement(@NotNull Player player) {
        this.grantAdvancement(player, this.netherAdvancement);
    }

    public void grantEndAdvancement(@NotNull Player player) {
        this.grantAdvancement(player, this.endAdvancement);
    }

    public Chat getChat() {
        return this.chat;
    }

    public static MinigameService getInstance() {
        return instance;
    }
}
