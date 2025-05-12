package io.mewb.Showcase.listeners;


import io.mewb.Showcase.Showcase;
import io.mewb.Showcase.commands.TrashCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryView;

public class InventoryCloseListener implements Listener {
    private final Showcase plugin;

    public InventoryCloseListener(Showcase plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        InventoryView view = event.getView();
        view.getTitle();
        plugin.getMessage(TrashCommand.TRASH_INVENTORY_TITLE_KEY);
    }
}