package timongcraft.system.commands;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import timongcraft.system.Main;
import timongcraft.system.util.PlayerUtils;

public class RebootCommand {
    static int seconds;
    static BukkitRunnable runnable = null;

    public static void register() {
        new CommandTree("reboot")
                .withShortDescription("Reboot the server after a specified time")
                .withUsage("/reboot")
                .withPermission("tgc-system.team")
                .then(new IntegerArgument("minutes", 1)
                        .then(new GreedyStringArgument("reason")
                                .executes(RebootCommand::rebootManager)))
                .register();
    }

    private static int rebootManager(CommandSender sender, CommandArguments args) {
        seconds = (int) args.get("minutes") * 60;
        String reason = (String) args.get("reason");

        sender.sendMessage(Main.get().getPrefix() + "Scheduled reboot in: " + args.get("minutes") + " minutes with reason: " + reason);
        PlayerUtils.sendToTeam(sender.getName(), null, "Scheduled reboot in: " + args.get("minutes") + " minutes with reason: " + reason);

        if (runnable != null) {
            runnable.cancel();
        }

        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (seconds == 600) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(Main.get().getConfig().getString("prefix.alertPrefix") + "The server will reboot in " + seconds / 60 + " minutes because: " + reason.replaceAll("&", "§"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, .5F);
                    }
                }
                if (seconds == 60 || seconds == 10 || seconds == 3 || seconds == 2 || seconds == 1) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(Main.get().getConfig().getString("prefix.alertPrefix") + "The server will reboot in " + seconds + " seconds because: " + reason.replaceAll("&", "§"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, .5F);
                    }
                }
                if (seconds == 0) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.kickPlayer("The server is rebooting because:\n" + reason.replaceAll("&", "§"));
                    }
                    cancel();
                    Bukkit.spigot().restart();
                    return;
                }
                seconds--;
            }
        };
        runnable.runTaskTimer(Main.get(), 0, 20);
        return 1;
    }
}
