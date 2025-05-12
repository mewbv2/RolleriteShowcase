package io.mewb.Showcase.listeners;


import io.mewb.Showcase.Showcase;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;


/**
 * I'm pretty sure there is an easier way to do this, but eh
 */

public class FoodLevelChangeListener implements Listener {
    private final Showcase plugin;

    public FoodLevelChangeListener(Showcase plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (plugin.godModePlayers.contains(player.getUniqueId())) {
                if (event.getFoodLevel() < 20) {
                    event.setFoodLevel(20);
                }

                event.setCancelled(true);
            }
        }
    }
}