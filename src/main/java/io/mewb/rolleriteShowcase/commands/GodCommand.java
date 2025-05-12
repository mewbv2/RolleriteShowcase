package io.mewb.rolleriteShowcase.commands;


import io.mewb.rolleriteShowcase.RolleriteShowcase;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class GodCommand implements CommandExecutor {

    private final RolleriteShowcase plugin;

    public GodCommand(RolleriteShowcase plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) { // /god
            if (!(sender instanceof Player player)) {
                plugin.sendMessage(sender, "console_cannot_use_self");
                return true;
            }
            if (!player.hasPermission("rollerite.god")) {
                plugin.sendMessage(player, "no_permission");
                return true;
            }
            toggleGodMode(player, player); // Target is self
            return true;

        } else if (args.length == 1) { // /god <player>
            if (!sender.hasPermission("rollerite.god.others")) {
                plugin.sendMessage(sender, "no_permission");
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                plugin.sendMessage(sender, "player_not_found", "%player%", args[0]);
                return true;
            }
            toggleGodMode(sender, target); // Sender is the one issuing, target is the one affected
            return true;

        } else {
            plugin.sendMessage(sender, "god_usage");
            return true;
        }
    }

    private void toggleGodMode(CommandSender informant, Player target) {
        UUID targetUUID = target.getUniqueId();
        if (plugin.godModePlayers.contains(targetUUID)) {
            plugin.godModePlayers.remove(targetUUID);
            target.setInvulnerable(false); // Reset invulnerability
            // Also reset any custom god mode flags if you implemented them beyond setInvulnerable
            if (informant.equals(target)) {
                plugin.sendMessage(target, "god_disabled_self");
            } else {
                plugin.sendMessage(target, "god_disabled_self"); // Message to target
                plugin.sendMessage(informant, "god_disabled_other", "%player%", target.getName()); // Message to informant
            }
        } else {
            plugin.godModePlayers.add(targetUUID);
            target.setInvulnerable(true); // Bukkit's built-in invulnerability
            target.setFoodLevel(20); // Max hunger
            target.setSaturation(20); // Max saturation
            if (informant.equals(target)) {
                plugin.sendMessage(target, "god_enabled_self");
            } else {
                plugin.sendMessage(target, "god_enabled_self"); // Message to target
                plugin.sendMessage(informant, "god_enabled_other", "%player%", target.getName()); // Message to informant
            }
        }
    }
}