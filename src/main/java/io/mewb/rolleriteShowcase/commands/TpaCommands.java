package io.mewb.rolleriteShowcase.commands;


import io.mewb.rolleriteShowcase.RolleriteShowcase;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;



public class TpaCommands implements CommandExecutor {

    private final RolleriteShowcase plugin;

    public TpaCommands(RolleriteShowcase plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.sendMessage(sender, "must_be_player");
            return true;
        }

        if (command.getName().equalsIgnoreCase("tpa")) {
            handleTpa(player, args);
        } else if (command.getName().equalsIgnoreCase("tpaccept")) {
            handleTpaccept(player, args);
        } else if (command.getName().equalsIgnoreCase("tpdeny")) {
            handleTpdeny(player, args);
        }
        return true;
    }

    private void handleTpa(Player sender, String[] args) {
        if (!sender.hasPermission("rollerite.tpa")) {
            plugin.sendMessage(sender, "no_permission");
            return;
        }
        if (args.length < 1) {
            plugin.sendMessage(sender, "tpa_usage");
            return;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            plugin.sendMessage(sender, "player_not_found", "%player%", args[0]);
            return;
        }
        if (target.equals(sender)) {
            plugin.sendMessage(sender, "tpa_cannot_teleport_to_self");
            return;
        }


        TeleportRequest existingRequest = plugin.teleportRequests.get(target.getUniqueId());
        if (existingRequest != null && existingRequest.getRequester().equals(sender.getUniqueId())) {
            existingRequest.isExpired();
        }// Optionally notify that the previous request is being replaced or just proceed.


        // Configurable?
        int TPA_TIMEOUT_SECONDS = 60;
        TeleportRequest request = new TeleportRequest(sender.getUniqueId(), target.getUniqueId(), System.currentTimeMillis() + (TPA_TIMEOUT_SECONDS * 1000L));
        plugin.teleportRequests.put(target.getUniqueId(), request); // Store by target, so target can easily find it

        plugin.sendMessage(sender, "tpa_request_sent", "%target%", target.getName(), "%timeout%", String.valueOf(TPA_TIMEOUT_SECONDS));
        plugin.sendMessage(target, "tpa_request_received", "%sender%", sender.getName());

        // Schedule expiration task
        new BukkitRunnable() {
            @Override
            public void run() {
                TeleportRequest currentRequest = plugin.teleportRequests.get(target.getUniqueId());
                if (currentRequest != null && currentRequest.getRequester().equals(sender.getUniqueId()) && !currentRequest.isAccepted()) {
                    if (currentRequest.isExpired() && !currentRequest.isHandled()) {
                        plugin.teleportRequests.remove(target.getUniqueId());
                        Player requesterOnline = Bukkit.getPlayer(sender.getUniqueId());
                        Player targetOnline = Bukkit.getPlayer(target.getUniqueId());
                        if (requesterOnline != null && requesterOnline.isOnline()) {
                            plugin.sendMessage(requesterOnline, "tpa_request_expired_sender", "%target%", target.getName());
                        }
                        if (targetOnline != null && targetOnline.isOnline()) {
                            plugin.sendMessage(targetOnline, "tpa_request_expired_target", "%sender%", sender.getName());
                        }
                        currentRequest.setHandled(true);
                    }
                }
            }
        }.runTaskLater(plugin, TPA_TIMEOUT_SECONDS * 20L);
    }

    private void handleTpaccept(Player acceptor, String[] args) {
        if (!acceptor.hasPermission("rollerite.tpa.accept")) {
            plugin.sendMessage(acceptor, "no_permission");
            return;
        }

        TeleportRequest requestToAccept = null;
        Player requester = null;

        if (args.length == 0) {
            if (plugin.teleportRequests.containsKey(acceptor.getUniqueId())) {
                TeleportRequest foundRequest = plugin.teleportRequests.get(acceptor.getUniqueId());
                if (foundRequest != null && !foundRequest.isExpired() && !foundRequest.isHandled()) {
                    requestToAccept = foundRequest;
                    requester = Bukkit.getPlayer(requestToAccept.getRequester());
                }
            }
            if (requestToAccept == null) {
                plugin.sendMessage(acceptor, plugin.getMessage("tpa_no_pending_request").replace("%player%", "anyone"));
                return;
            }
        } else { // /tpaccept <player>
            requester = Bukkit.getPlayerExact(args[0]);
            if (requester == null) {
                plugin.sendMessage(acceptor, "player_not_found", "%player%", args[0]);
                return;
            }
            TeleportRequest specificRequest = plugin.teleportRequests.get(acceptor.getUniqueId());
            if (specificRequest != null && specificRequest.getRequester().equals(requester.getUniqueId()) && !specificRequest.isExpired() && !specificRequest.isHandled()) {
                requestToAccept = specificRequest;
            } else {
                plugin.sendMessage(acceptor, "tpa_no_pending_request", "%player%", requester.getName());
                return;
            }
        }


        if (requester == null || !requester.isOnline()) {
            if (requester != null) plugin.sendMessage(acceptor, "tpa_no_pending_request", "%player%", requester.getName());
            else plugin.sendMessage(acceptor, plugin.getMessage("tpa_no_pending_request").replace("%player%", "anyone"));
            return;
        }

        if (requestToAccept.isExpired() || requestToAccept.isHandled()) {
            plugin.sendMessage(acceptor, "tpa_request_expired_target", "%sender%", requester.getName());
            plugin.teleportRequests.remove(acceptor.getUniqueId()); // Clean up
            return;
        }

        requestToAccept.setAccepted(true);
        requestToAccept.setHandled(true);

        plugin.sendMessage(requester, "tpa_request_accepted_sender", "%target%", acceptor.getName());
        plugin.sendMessage(acceptor, "tpa_request_accepted_target", "%sender%", requester.getName());

        plugin.sendMessage(requester, "tpa_teleporting");
        requester.teleport(acceptor.getLocation());

        plugin.teleportRequests.remove(acceptor.getUniqueId()); // Clean up the request
    }


    private void handleTpdeny(Player denier, String[] args) {
        if (!denier.hasPermission("rollerite.tpa.deny")) {
            plugin.sendMessage(denier, "no_permission");
            return;
        }

        TeleportRequest requestToDeny = null;
        Player requester = null;

        if (args.length == 0) {
            if (plugin.teleportRequests.containsKey(denier.getUniqueId())) {
                TeleportRequest foundRequest = plugin.teleportRequests.get(denier.getUniqueId());
                if (foundRequest != null && !foundRequest.isExpired() && !foundRequest.isHandled()) {
                    requestToDeny = foundRequest;
                    requester = Bukkit.getPlayer(requestToDeny.getRequester());
                }
            }
            if (requestToDeny == null) {
                plugin.sendMessage(denier, plugin.getMessage("tpa_no_pending_request").replace("%player%", "anyone"));
                return;
            }
        } else {
            requester = Bukkit.getPlayerExact(args[0]);
            if (requester == null) {
                plugin.sendMessage(denier, "player_not_found", "%player%", args[0]);
                return;
            }
            TeleportRequest specificRequest = plugin.teleportRequests.get(denier.getUniqueId());
            if (specificRequest != null && specificRequest.getRequester().equals(requester.getUniqueId()) && !specificRequest.isExpired() && !specificRequest.isHandled()) {
                requestToDeny = specificRequest;
            } else {
                plugin.sendMessage(denier, "tpa_no_pending_request", "%player%", requester.getName());
                return;
            }
        }


        if (requester == null) {
            plugin.sendMessage(denier, plugin.getMessage("tpa_no_pending_request").replace("%player%", "anyone"));
            return;
        }

        if (requestToDeny.isExpired() || requestToDeny.isHandled()) {
            // No specific message for denying an expired request usually, just remove it.
            plugin.teleportRequests.remove(denier.getUniqueId());
            return;
        }
        requestToDeny.setHandled(true); // Mark as handled (denied)

        plugin.sendMessage(requester, "tpa_request_denied_sender", "%target%", denier.getName());
        plugin.sendMessage(denier, "tpa_request_denied_target", "%sender%", requester.getName());

        plugin.teleportRequests.remove(denier.getUniqueId()); // Clean up
    }
}


