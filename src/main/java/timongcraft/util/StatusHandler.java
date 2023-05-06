package timongcraft.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import timongcraft.Main;

import java.util.ArrayList;
import java.util.List;

public class StatusHandler {
    public static String getStatus(Player player) {
        String status = Main.get().getDataConfig().getString("players." + player.getUniqueId() + ".status");

        if(status == "") {
            return null;
        } else {
            return status;
        }
    }

    public static String getStatusWithBrackets(Player player) {
        return "§f[" + Main.get().getDataConfig().getString("players." + player.getUniqueId() + ".status") + "§f] ";
    }

    public static List<String> getAllStatuses() {
        List<String> Statuses = new ArrayList<>();

        for(Player player : Bukkit.getOnlinePlayers()) {
            if(Main.get().getDataConfig().isSet("players." + player.getUniqueId() + ".status")) {
                Statuses.add(Main.get().getDataConfig().getString("players." + player.getUniqueId() + ".status"));
            }
        }
        return Statuses;
    }

    public static void setStatus(Player player, String status) {
        Main.get().getDataConfig().set("players." + player.getUniqueId() + ".status", status);
        Main.get().getDataConfig().save();
    }

    public static void setAllPlayerTeams() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            setPlayerTeams(player);
        }
    }

    private static void setPlayerTeams(Player player) {
        for (String status : getAllStatuses()) {
            Team team = player.getScoreboard().getTeam(status);
            if(team == null && status != "") {
                team = player.getScoreboard().registerNewTeam(status);
                team.setPrefix("§f[" + status + "§f] ");
            }

            for(Player onlinePlayers : Bukkit.getOnlinePlayers()) {
                String onlinePlayerStatus = getStatus(onlinePlayers);
                if(onlinePlayerStatus != null && onlinePlayerStatus.equals(status)) {
                    team.addEntry(onlinePlayers.getName());
                }
            }

            for(Player onlinePlayers : Bukkit.getOnlinePlayers()) {
                String onlinePlayerStatus = getStatus(onlinePlayers);
                if(onlinePlayerStatus != null && onlinePlayerStatus != "") continue;

                Team defaultTeam = player.getScoreboard().getTeam("default");
                if(defaultTeam == null) {
                    defaultTeam = player.getScoreboard().registerNewTeam("default");
                }
                defaultTeam.addEntry(onlinePlayers.getName());
            }
        }
    }
}
