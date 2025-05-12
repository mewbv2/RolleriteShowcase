package io.mewb.rolleriteShowcase;

import io.mewb.rolleriteShowcase.commands.*;
import io.mewb.rolleriteShowcase.listeners.FoodLevelChangeListener;
import io.mewb.rolleriteShowcase.listeners.InventoryCloseListener;
import io.mewb.rolleriteShowcase.listeners.PlayerDamageListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;



import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class RolleriteShowcase extends JavaPlugin {

    private static RolleriteShowcase instance;
    public final HashSet<UUID> godModePlayers = new HashSet<>();
    public final HashMap<UUID, TeleportRequest> teleportRequests = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        reloadConfig();
        // Keep this if you still need direct config access elsewhere
        FileConfiguration config = getConfig();

        // Register commands
        getCommand("gamemode").setExecutor(new GamemodeCommand(this));
        getCommand("god").setExecutor(new GodCommand(this));
        getCommand("openinv").setExecutor(new OpenInvCommand(this));
        getCommand("enderchest").setExecutor(new EnderChestCommand(this)); // Ensure this is the revised one
        getCommand("fix").setExecutor(new FixCommand(this));
        TpaCommands tpaCmds = new TpaCommands(this);
        getCommand("tpa").setExecutor(tpaCmds);
        getCommand("tpaccept").setExecutor(tpaCmds);
        getCommand("tpdeny").setExecutor(tpaCmds);
        getCommand("trash").setExecutor(new TrashCommand(this));

        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerDamageListener(this), this);
        getServer().getPluginManager().registerEvents(new FoodLevelChangeListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryCloseListener(this), this);


        getLogger().info("EssentialsPlus has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("EssentialsPlus has been disabled!");
        godModePlayers.clear();
        teleportRequests.clear();
    }

    public static RolleriteShowcase getInstance() {
        return instance;
    }

    public String getMessage(String path, String... replacements) {

        String message = getConfig().getString(path, "&cMessage not found in config: " + path);


        String prefix = getConfig().getString("prefix", "&7[&bEssentials&c+&7] &r"); // Default if prefix is missing


        message = message.replace("%prefix%", prefix);


        if (replacements != null) {
            for (int i = 0; i < replacements.length; i += 2) {
                if (i + 1 < replacements.length) {
                    String placeholder = replacements[i];
                    String value = replacements[i + 1];
                    if (placeholder != null && value != null) {
                        message = message.replace(placeholder, value);
                    }
                }
            }
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public void sendMessage(CommandSender sender, String path, String... replacements) {
        sender.sendMessage(getMessage(path, replacements));
    }

    public boolean handleConsoleSelfCommand(CommandSender sender, String permissionNode) {
        if (!(sender instanceof Player)) {
            sendMessage(sender, "console_cannot_use_self");
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission(permissionNode)) {
            sendMessage(player, "no_permission");
            return true;
        }
        return false;
    }
}