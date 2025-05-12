package io.mewb.Showcase.commands;


import io.mewb.Showcase.Showcase;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class OpenInvCommand implements CommandExecutor {

    private final Showcase plugin;

    public OpenInvCommand(Showcase plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.sendMessage(sender, "must_be_player"); // Only players can open inventories
            return true;
        }

        if (!player.hasPermission("rollerite.openinv")) {
            plugin.sendMessage(player, "no_permission");
            return true;
        }

        if (args.length != 1) {
            plugin.sendMessage(player, "openinv_usage");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            plugin.sendMessage(player, "player_not_found", "%player%", args[0]);
            return true;
        }


        player.openInventory(target.getInventory());
        plugin.sendMessage(player, "openinv_opening", "%player%", target.getName());
        return true;
    }
}