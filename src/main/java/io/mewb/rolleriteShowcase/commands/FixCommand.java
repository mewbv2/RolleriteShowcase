package io.mewb.rolleriteShowcase.commands;


import io.mewb.rolleriteShowcase.RolleriteShowcase;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class FixCommand implements CommandExecutor {

    private final RolleriteShowcase plugin;

    public FixCommand(RolleriteShowcase plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.sendMessage(sender, "must_be_player");
            return true;
        }

        if (!player.hasPermission("rollerite.fix")) {
            plugin.sendMessage(player, "no_permission");
            return true;
        }

        if (args.length != 0) {
            plugin.sendMessage(player, "fix_usage");
            return true;
        }

        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand.getType() == Material.AIR) {
            plugin.sendMessage(player, "fix_no_item");
            return true;
        }

        ItemMeta meta = itemInHand.getItemMeta();
        if (meta instanceof Damageable damageableMeta) {
            if (damageableMeta.hasDamage()) {
                damageableMeta.setDamage(0);
                itemInHand.setItemMeta(damageableMeta);
                plugin.sendMessage(player, "fix_success");
            } else {
                // Item is already at full durability or cannot be damaged further
                plugin.sendMessage(player, "fix_not_repairable"); // Or a more specific message
            }
        } else {
            plugin.sendMessage(player, "fix_not_repairable");
        }
        return true;
    }
}