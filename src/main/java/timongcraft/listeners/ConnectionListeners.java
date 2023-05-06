package timongcraft.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import timongcraft.Main;
import timongcraft.commands.MaintenanceCommand;
import timongcraft.util.StatusHandler;

import java.io.File;
import java.util.HexFormat;
import java.util.List;
import java.util.Random;

public class ConnectionListeners implements Listener {

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        motdsHandler(event);

        maintenanceServerPingHandler(event);
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        Main.get().getDataConfig().set("players." + player.getUniqueId() + ".name", player.getName());
        Main.get().getDataConfig().save();

        maintenanceJoinHandler(player, event);

        permissionJoinHandler(player);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Main.get().getDataConfig().set("players." + player.getUniqueId() + ".ipAdress", player.getAddress().toString().substring(1).split(":")[0]);
        Main.get().getDataConfig().save();

        resourcePackJoinHandler(player);

        playerJoinMessageJoinHandler(player, event);

        teamJoinHandler(event, player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        playerNameQuitHandler(player, event);

        teamQuitHandler(event, player);
    }

    private void motdsHandler(ServerListPingEvent event) {
        if(Main.get().getConfig().getBoolean("motds.enabled") && !Main.get().getConfig().getStringList("motds.list").isEmpty()) {
            String motd = Main.get().getConfig().getStringList("motds.list").get(new Random().nextInt(Main.get().getConfig().getStringList("motds.list").size()));
            event.setMotd(motd.replaceAll("&", "§"));
        }
    }

    private void maintenanceServerPingHandler(ServerListPingEvent event) {
        if(Main.get().getDataConfig().getBoolean("maintenance.enabled")) {
            event.setMotd(Main.get().getConfig().getString("maintenance.motd"));
            event.setMaxPlayers(0);
            if(Main.get().getConfig().getBoolean("maintenance.icon")) {
                File maintenanceIcon = new File(Main.get().getDataFolder(), "maintenance-icon.png");
                if(maintenanceIcon.exists()) {
                    try {
                        event.setServerIcon(Bukkit.loadServerIcon(maintenanceIcon));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private void permissionJoinHandler(Player player) {
        List<String> playerPermissions = Main.get().getDataConfig().getStringList("players." + player.getUniqueId() + ".permissions");
        List<String> playerGroups = Main.get().getDataConfig().getStringList("players." + player.getUniqueId() + ".groups");

        if(!playerGroups.contains("default")) playerGroups.add("default");

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

    private void maintenanceJoinHandler(Player player, PlayerLoginEvent event) {
        if(Main.get().getDataConfig().getBoolean("maintenance.enabled") && !MaintenanceCommand.isAllowed(player)) {
            Main.get().getLogger().info(Main.get().getPrefix() + player.getName() + " tried to join while maintenance mode");
            for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
                if(onlinePlayers.hasPermission("tgc-system.team")) {
                    onlinePlayers.sendMessage(Main.get().getPrefix() + player.getName() + " tried to join while maintenance mode");
                }
            }
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Main.get().getConfig().getString("maintenance.kickMessage"));
        }
    }

    private void resourcePackJoinHandler(Player player) {
        if(Main.get().getConfig().isSet("resourcePack.url") && !Main.get().getConfig().getString("resourcePack.url").isEmpty()) {
            final String url = Main.get().getConfig().getString("resourcePack.url");
            final byte[] hash = HexFormat.of().parseHex(Main.get().getConfig().getString("resourcePack.hash"));
            final String promt = Main.get().getConfig().getString("resourcePack.promt");
            final boolean force = Main.get().getConfig().getBoolean("resourcePack.force");

            if(url != null && hash != null && !player.hasPermission("tgc-system.team")) {
                player.setResourcePack(url, hash, promt, force);
            }

            if(url != null && hash != null && player.hasPermission("tgc-system.team") && Main.get().getDataConfig().getBoolean("players." + player.getUniqueId() + ".resourcepack")) {
                player.setResourcePack(url, hash, false);
            }
        }
    }

    private void teamJoinHandler(PlayerJoinEvent event, Player player) {
        if(player.hasPermission("tgc-system.team")) {
            for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if(onlinePlayer.hasPermission("tgc-system.team") && onlinePlayer.getGameMode().equals(GameMode.SPECTATOR)) player.hidePlayer(Main.get(), onlinePlayer);

            }

            if(player.getGameMode().equals(GameMode.SPECTATOR) && Bukkit.getOnlinePlayers().size() > 1) {
                event.setJoinMessage(null);

                for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if(onlinePlayer.hasPermission("tgc-system.team")) {
                        onlinePlayer.sendMessage(Main.get().getConfig().getString("joinQuitMessage.joinMessage").replaceAll("%Player%",player.getName().replaceAll("&", "§")));
                    }
                }
            }
        }
    }

    private void teamQuitHandler(PlayerQuitEvent event, Player player) {
        if(player.hasPermission("tgc-system.team") && player.getGameMode().equals(GameMode.SPECTATOR) && Bukkit.getOnlinePlayers().size() > 1) {
            event.setQuitMessage(null);

            for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if(onlinePlayer.hasPermission("tgc-system.team")) {
                    onlinePlayer.sendMessage(Main.get().getConfig().getString("joinQuitMessage.quitMessage").replaceAll("%Player%",player.getName().replaceAll("&", "§")));
                }
            }
        }
    }

    private void playerJoinMessageJoinHandler(Player player, PlayerJoinEvent event) {
        if(Main.get().getConfig().getBoolean("joinQuitMessage.enabled")) {
            event.setJoinMessage(Main.get().getConfig().getString("joinQuitMessage.joinMessage").replaceAll("%Player%",player.getName().replaceAll("&", "§")));
        }

        if(Main.get().getConfig().getBoolean("onJoin.enabled")){
            if(Main.get().getConfig().getString("onJoin.message") != null) {
                player.sendMessage(Main.get().getConfig().getString("onJoin.message").replaceAll("%AlertPrefix%", Main.get().getConfig().getString("prefix.alertPrefix").replaceAll("&", "§")));
            }
        }

        String status = StatusHandler.getStatus(player);
        if(Main.get().getConfig().getBoolean("onJoin.status")){
            if(status != null) {
                player.sendMessage(Main.get().getPrefix() + "§aYour status is set to: " + status.replaceAll("&", "§"));
            }
        }
    }

    private void playerNameQuitHandler(Player player, PlayerQuitEvent event) {
        if(Main.get().getConfig().getBoolean("joinQuitMessage.enabled")) {
            event.setQuitMessage(Main.get().getConfig().getString("joinQuitMessage.quitMessage").replaceAll("%Player%",player.getName().replaceAll("&", "§")));
        }
    }
    
}