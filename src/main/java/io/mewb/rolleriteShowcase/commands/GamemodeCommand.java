package io.mewb.rolleriteShowcase.commands;


import io.mewb.rolleriteShowcase.RolleriteShowcase;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GamemodeCommand implements CommandExecutor {

    private final RolleriteShowcase plugin;

    public GamemodeCommand(RolleriteShowcase plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            plugin.sendMessage(sender, "gamemode_usage");
            return true;
        }

        GameMode gameMode;
        String gamemodeArg = args[0].toLowerCase();

        switch (gamemodeArg) {
            case "survival":
            case "s":
            case "0":
                gameMode = GameMode.SURVIVAL;
                break;
            case "creative":
            case "c":
            case "1":
                gameMode = GameMode.CREATIVE;
                break;
            case "adventure":
            case "a":
            case "2":
                gameMode = GameMode.ADVENTURE;
                break;
            case "spectator":
            case "sp":
            case "3":
                gameMode = GameMode.SPECTATOR;
                break;
            default:
                plugin.sendMessage(sender, "gamemode_invalid_type");
                return true;
        }

        if (args.length == 1) { // /gamemode <type>
            if (!(sender instanceof Player)) {
                plugin.sendMessage(sender, "console_cannot_use_self");
                return true;
            }
            Player player = (Player) sender;
            if (!player.hasPermission("rollerite.gamemode")) {
                plugin.sendMessage(player, "no_permission");
                return true;
            }

            player.setGameMode(gameMode);
            plugin.sendMessage(player, "gamemode_set_self", "%gamemode%", gameMode.toString().toLowerCase());
            return true;

        } else if (args.length == 2) { // /gamemode <type> <player>
            if (!sender.hasPermission("rollerite.gamemode.others")) {
                plugin.sendMessage(sender, "no_permission");
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                plugin.sendMessage(sender, "player_not_found", "%player%", args[1]);
                return true;
            }

            target.setGameMode(gameMode);
            plugin.sendMessage(target, "gamemode_set_self", "%gamemode%", gameMode.toString().toLowerCase()); // Message to target
            plugin.sendMessage(sender, "gamemode_set_other", "%player%", target.getName(), "%gamemode%", gameMode.toString().toLowerCase()); // Message to sender
            return true;

        } else {
            plugin.sendMessage(sender, "gamemode_usage");
            return true;
        }
    }
}