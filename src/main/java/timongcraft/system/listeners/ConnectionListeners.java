package timongcraft.system.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import timongcraft.system.Main;
import timongcraft.system.commands.MaintenanceCommand;
import timongcraft.system.util.StatusHandler;

import java.io.File;
import java.util.*;

public class ConnectionListeners implements Listener {

    public static final List<UUID> resourcePackLoaded = new ArrayList<>();

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        motdHandler(event);

        maintenanceServerPingHandler(event);
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        Main.get().getDataConfig().set("players." + player.getUniqueId() + ".name", player.getName());
        Main.get().getDataConfig().save();

        maintenanceJoinHandler(player, event);

        permissionJoinHandler(player);

        StatusHandler.refreshStatus(player);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Main.get().getDataConfig().set("players." + player.getUniqueId() + ".ipAddress", player.getAddress().toString().substring(1).split(":")[0]);
        Main.get().getDataConfig().save();

        resourcePackJoinHandler(player);

        playerJoinMessageJoinHandler(player, event);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        playerNameQuitHandler(player, event);
    }

    private void motdHandler(ServerListPingEvent event) {
        if (Main.get().getConfig().getBoolean("motds.hiddenMode")) {
            String ipAddress = event.getAddress().toString().substring(1).split(":")[0];
            List<String> knownIpAddresses = new ArrayList<>();

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!Main.get().getDataConfig().isSet("players." + player.getUniqueId() + ".ipAddress")) continue;
                knownIpAddresses.add(Main.get().getDataConfig().getString("players." + player.getUniqueId() + ".ipAddress"));
            }

            if (!knownIpAddresses.contains(ipAddress)) {
                event.setMotd("A Minecraft Server");
                event.setServerIcon(null);
                event.setMaxPlayers(20);
                return;
            }
        }

        if (Main.get().getConfig().getBoolean("motds.enabled") && !Main.get().getConfig().getStringList("motds.list").isEmpty()) {
            String motd = Main.get().getConfig().getStringList("motds.list").get(new Random().nextInt(Main.get().getConfig().getStringList("motds.list").size()));
            event.setMotd(motd.replaceAll("&", "§"));
        }
    }

    private void maintenanceServerPingHandler(ServerListPingEvent event) {
        if (!Main.get().getDataConfig().getBoolean("maintenance.enabled")) return;
        event.setMotd(Main.get().getConfig().getString("maintenance.motd"));
        event.setMaxPlayers(0);
        if (!Main.get().getConfig().getBoolean("maintenance.icon")) return;
        File maintenanceIcon = new File(Main.get().getDataFolder(), "maintenance-icon.png");
        if (!maintenanceIcon.exists()) return;
        try {
            event.setServerIcon(Bukkit.loadServerIcon(maintenanceIcon));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void maintenanceJoinHandler(Player player, PlayerLoginEvent event) {
        if (!Main.get().getDataConfig().getBoolean("maintenance.enabled") || MaintenanceCommand.isAllowed(player.getUniqueId()))
            return;
        Main.get().getLogger().info(Main.get().getPrefix() + player.getName() + " tried to join while maintenance mode");
        Bukkit.broadcast(Main.get().getPrefix() + player.getName() + " tried to join while maintenance mode", "tgc-system.team");
        event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Main.get().getConfig().getString("maintenance.kickMessage"));
    }

    private void permissionJoinHandler(Player player) {
        List<String> playerPermissions = Main.get().getDataConfig().getStringList("players." + player.getUniqueId() + ".permissions");
        List<String> playerGroups = Main.get().getDataConfig().getStringList("players." + player.getUniqueId() + ".groups");

        if (!playerGroups.contains("default")) playerGroups.add("default");

        for (String permissionString : playerPermissions) {
            String[] permissionParts = permissionString.split(":");
            player.addAttachment(Main.get(), permissionParts[0], Boolean.parseBoolean(permissionParts[1]));
        }

        for (String groupName : playerGroups) {
            List<String> groupPermissions = Main.get().getDataConfig().getStringList("groups." + groupName + ".permissions");
            for (String permissionString : groupPermissions) {
                String[] parts = permissionString.split(":");
                String permission = parts[0];
                boolean value = Boolean.parseBoolean(parts[1]);
                player.addAttachment(Main.get(), permission, value);
            }
        }
    }

    private void resourcePackJoinHandler(Player player) {
        if (Main.get().getConfig().isSet("resourcePack.url") && !Main.get().getConfig().getString("resourcePack.url").isEmpty()) {
            final String url = Main.get().getConfig().getString("resourcePack.url");
            final byte[] hash = HexFormat.of().parseHex(Main.get().getConfig().getString("resourcePack.hash"));
            final String promt = Main.get().getConfig().getString("resourcePack.promt");
            final boolean force = Main.get().getConfig().getBoolean("resourcePack.force");

            if (url != null && hash != null && !player.hasPermission("tgc-system.team"))
                player.setResourcePack(url, hash, promt, force);

            if (url != null && hash != null && player.hasPermission("tgc-system.team") && Main.get().getDataConfig().getBoolean("players." + player.getUniqueId() + ".resourcepack"))
                player.setResourcePack(url, hash, false);

            if (!Main.get().getConfig().getBoolean("resourcePack.force")) return;

            Bukkit.getScheduler().runTaskLater(Main.get(), () -> {
                if (!player.isOnline() || player.hasPermission("tgc-system.team")) return;

                if (!resourcePackLoaded.contains(player.getUniqueId()))
                    player.kickPlayer("§cYou must use the server's resource pack.");
                resourcePackLoaded.remove(player.getUniqueId());
            }, Main.get().getConfig().isSet("resourcePack.maxLoadTime") ? Main.get().getConfig().getInt("resourcePack.maxLoadTime") * 20L : 300L);
        }
    }

    private void playerJoinMessageJoinHandler(Player player, PlayerJoinEvent event) {
        if (Main.get().getConfig().getBoolean("joinQuitMessage.enabled"))
            event.setJoinMessage(Main.get().getConfig().getString("joinQuitMessage.joinMessage").replaceAll("%Player%", player.getName()).replaceAll("&", "§"));

        if (Main.get().getConfig().getBoolean("onJoin.enabled"))
            if (Main.get().getConfig().getString("onJoin.message") != null)
                player.sendMessage(Main.get().getConfig().getString("onJoin.message").replaceAll("%prefix%", Main.get().getPrefix().replaceAll("%alertPrefix%", Main.get().getConfig().getString("prefix.alertPrefix")).replaceAll("&", "§")));

        String status = StatusHandler.getStatus(player);
        if (Main.get().getConfig().getBoolean("onJoin.status"))
            if (status != null)
                player.sendMessage(Main.get().getPrefix() + "Your status is set to: " + status.replaceAll("&", "§"));
    }

    private void playerNameQuitHandler(Player player, PlayerQuitEvent event) {
        if (Main.get().getConfig().getBoolean("joinQuitMessage.enabled"))
            event.setQuitMessage(Main.get().getConfig().getString("joinQuitMessage.quitMessage").replaceAll("%Player%", player.getName()).replaceAll("&", "§"));
    }

}