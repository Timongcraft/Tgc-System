package timongcraft.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import timongcraft.Main;

public class TeamUtils {
    public static void sendToTeam(String sender, String target, boolean NotToTarget, String msg) {
        for(Player teamPlayer : Bukkit.getOnlinePlayers()) {
            if(teamPlayer.hasPermission("tgc-system.team") && !(teamPlayer.getName().equals(sender))) {
                if(NotToTarget && teamPlayer.getName().equals(target)) return;

                teamPlayer.sendMessage(Main.get().getPrefix() + "§7§o[" + sender + "§7§o: " + msg + "§7§o ]");
            }
        }
    }
}
