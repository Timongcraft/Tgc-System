package timongcraft.util;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import timongcraft.Main;

public class PlayerUtils {
    public static void sendToTeam(String sender, String target, String msg) {
        for(Player teamPlayer : Bukkit.getOnlinePlayers()) {
            if(teamPlayer.hasPermission("tgc-system.team") && !(teamPlayer.getName().equals(sender))) {
                if(target != null && teamPlayer.getName().equals(target)) return;

                teamPlayer.sendMessage(Main.get().getPrefix() + "§7§o[" + sender + "§7§o: " + msg + "§7§o]");
            }
        }
    }

    public static String getPlayerNameWithStatus(Player player, boolean statusEnabled) {
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(player.getName());
        String teamPrefix = team != null ? team.getColor() + (!team.getPrefix().isEmpty() ? (team.getPrefix() + ChatColor.RESET) : "") : "";
        String teamSuffix = team != null ? team.getColor() + (!team.getSuffix().isEmpty() ? (team.getSuffix() + ChatColor.RESET) : "") : "";

        if(statusEnabled) return teamPrefix + StatusHandler.getStatusWithBrackets(player) + player.getName() + teamSuffix;
        return teamPrefix + player.getName() + teamSuffix;
    }
}
