package timongcraft.util;

import org.bukkit.Bukkit;
import timongcraft.Main;

public class AutoSave {
    public AutoSave() {
        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.get(), () -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");
            Bukkit.getLogger().info("[Saved the game]");
        }, parseInterval(Main.get().getConfig().getString("autoSave.time")), parseInterval(Main.get().getConfig().getString("autoSave.time")));
        if(taskId == -1) {
            Main.get().getLogger().info("[Failed to schedule auto save task]");
        }
    }

    private long parseInterval(String interval) {
        if(interval == null) return 30 * 60 * 1000;
        int number = Integer.parseInt(interval.substring(0, interval.length() - 1));
        char unit = interval.charAt(interval.length() - 1);
        if(unit == 'm') return (long) number * 60 * 1000;
        else if(unit == 'h') return (long) number * 60 * 60 * 1000;
        else return 30 * 60 * 1000;
    }
}

