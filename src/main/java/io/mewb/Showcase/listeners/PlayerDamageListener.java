package io.mewb.Showcase.listeners;


import io.mewb.Showcase.Showcase;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * "God Mode"
 */

public class PlayerDamageListener implements Listener {
    private final Showcase plugin;

    public PlayerDamageListener(Showcase plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (plugin.godModePlayers.contains(player.getUniqueId())) {
                event.setCancelled(true);
                // Optional: Visual feedback like particles or sound if desired
            }
        }
    }
}