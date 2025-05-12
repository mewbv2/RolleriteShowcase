package io.mewb.rolleriteShowcase.commands;


import io.mewb.rolleriteShowcase.RolleriteShowcase;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EnderChestCommand implements CommandExecutor {

    private final RolleriteShowcase plugin;

    public EnderChestCommand(RolleriteShowcase plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Player targetPlayer = null; // The player whose Ender Chest will be opened
        Player opener = null;       // The player who will see the Ender Chest

        if (sender instanceof Player) {
            opener = (Player) sender;
        } else {
            // Console cannot open an inventory visually.
            // If a player argument is provided, it could theoretically be for data access later,
            // but not for opening an inventory.
            plugin.sendMessage(sender, "must_be_player");
            return true;
        }

        if (args.length == 0) { // /enderchest
            if (!opener.hasPermission("rollerite.enderchest")) {
                plugin.sendMessage(opener, "no_permission");
                return true;
            }
            targetPlayer = opener; // Player opens their own Ender Chest
            opener.openInventory(targetPlayer.getEnderChest());
            plugin.sendMessage(opener, "enderchest_opening_self");

        } else if (args.length == 1) { // /enderchest <player>
            if (!opener.hasPermission("rollerite.enderchest.others")) {
                plugin.sendMessage(opener, "no_permission");
                return true;
            }
            targetPlayer = Bukkit.getPlayerExact(args[0]);
            if (targetPlayer == null) {
                plugin.sendMessage(opener, "player_not_found", "%player%", args[0]);
                return true;
            }
            opener.openInventory(targetPlayer.getEnderChest());
            if (opener.equals(targetPlayer)) {
                plugin.sendMessage(opener, "enderchest_opening_self");
            } else {
                plugin.sendMessage(opener, "enderchest_opening_other", "%player%", targetPlayer.getName());
            }
        } else {
            plugin.sendMessage(opener, "enderchest_usage");
            return true;
        }
        return true;
    }
}