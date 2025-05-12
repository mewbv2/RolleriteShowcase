package io.mewb.rolleriteShowcase.listeners;


import io.mewb.rolleriteShowcase.RolleriteShowcase;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * "God Mode"
 */

public class PlayerDamageListener implements Listener {
    private final RolleriteShowcase plugin;

    public PlayerDamageListener(RolleriteShowcase plugin) {
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