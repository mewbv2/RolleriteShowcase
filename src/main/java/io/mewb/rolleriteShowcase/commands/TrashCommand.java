package io.mewb.rolleriteShowcase.commands;


import io.mewb.rolleriteShowcase.RolleriteShowcase;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;


public class TrashCommand implements CommandExecutor {

    private final RolleriteShowcase plugin;
    public static final String TRASH_INVENTORY_TITLE_KEY = "trash_title";

    public TrashCommand(RolleriteShowcase plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.sendMessage(sender, "must_be_player");
            return true;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("rollerite.trash")) {
            plugin.sendMessage(player, "no_permission");
            return true;
        }


        // could also use InventoryType.DISPENSER for a smaller one.
        Inventory trashInventory = Bukkit.createInventory(null, InventoryType.CHEST, plugin.getMessage(TRASH_INVENTORY_TITLE_KEY));
        player.openInventory(trashInventory);
        plugin.sendMessage(player, "trash_opened");

        return true;
    }
}