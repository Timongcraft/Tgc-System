package timongcraft.system.util;

import org.bukkit.entity.Player;
import timongcraft.system.Main;

public class StatusHandler {
    public static String getStatus(Player player) {
        return Main.get().getDataConfig().isSet("players." + player.getUniqueId() + ".status") ? Main.get().getDataConfig().getString("players." + player.getUniqueId() + ".status") : null;
    }

    public static String getStatusWithBrackets(Player player) {
        String status = Main.get().getDataConfig().getString("players." + player.getUniqueId() + ".status");

        if(status == null) return "";

        return "§f[" + status + "§f] ";
    }

    public static void setStatus(Player player, String status) {
        Main.get().getDataConfig().set("players." + player.getUniqueId() + ".status", status);
        Main.get().getDataConfig().save();

        player.setDisplayName(new PlayerUtils().getPlayerNameWithStatus(player, true));
        player.setPlayerListName(new PlayerUtils().getPlayerNameWithStatus(player, true));
    }

    public static void refreshStatus(Player player) {
        player.setDisplayName(new PlayerUtils().getPlayerNameWithStatus(player, true));
        player.setPlayerListName(new PlayerUtils().getPlayerNameWithStatus(player, true));
    }
}
