package timongcraft.util;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class AutoSaveHandler extends BukkitRunnable {
    @Override
    public void run() {
        Bukkit.getLogger().info("[AutoSave] saving the game..");
        TeamUtils.sendToTeam("AutoSave", null, "saving the game...");

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");
    }

    public long parseInterval(String interval) {
        if(interval == null) return 30 * 60 * 20;
        int number = Integer.parseInt(interval.substring(0, interval.length() - 1));
        char unit = interval.charAt(interval.length() - 1);
        if(unit == 'm') return (long) number * 60 * 20;
        else if(unit == 'h') return (long) number * 60 * 60 * 20;
        else return 30 * 60 * 20;
    }
}

