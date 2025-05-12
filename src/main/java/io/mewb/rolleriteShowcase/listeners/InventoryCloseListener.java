package io.mewb.rolleriteShowcase.listeners;


import io.mewb.rolleriteShowcase.RolleriteShowcase;
import io.mewb.rolleriteShowcase.commands.TrashCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryView;

public class InventoryCloseListener implements Listener {
    private final RolleriteShowcase plugin;

    public InventoryCloseListener(RolleriteShowcase plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        InventoryView view = event.getView();
        view.getTitle();
        plugin.getMessage(TrashCommand.TRASH_INVENTORY_TITLE_KEY);
    }
}