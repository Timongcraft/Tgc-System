package timongcraft.commands;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.executors.CommandExecutor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import timongcraft.Main;

public class RebootCommand {
    static int seconds;
    public static void register() {
        new CommandTree("reboot")
                .withFullDescription("Reboot the server after a specified time")
                .withPermission("tgc-system.team")
                .then(new IntegerArgument("minutes", 1, 59)
                        .then(new GreedyStringArgument("reason")
                                .executes(new RebootExecutor())))
                .register();
    }

    private static class RebootExecutor implements CommandExecutor {
        @Override
        public void run(CommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException {
            seconds = (int) args.get("minutes") * 60;
            String reason = (String) args.get("reason");

            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    if(seconds == 600) {
                        for(Player player : Bukkit.getOnlinePlayers()) {
                            player.sendMessage(Main.get().getConfig().getString("prefix.alertPrefix") + "The server will restart in " + seconds / 60 + " minutes because " + reason.replaceAll("&", "§"));
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F,.5F);
                        }
                    }
                    if(seconds == 60 || seconds == 10|| seconds == 3|| seconds == 2|| seconds == 1) {
                        for(Player player : Bukkit.getOnlinePlayers()) {
                            player.sendMessage(Main.get().getConfig().getString("prefix.alertPrefix") + "The server will restart in " + seconds + " seconds because " + reason.replaceAll("&", "§"));
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F,.5F);
                        }
                    }
                    if(seconds == 0) {
                        for(Player player : Bukkit.getOnlinePlayers()) {
                            player.kickPlayer("The server is restarting because:\n" + reason.replaceAll("&", "§"));
                        }
                        cancel();
                        Bukkit.spigot().restart();
                        return;
                    }
                    seconds--;
                }
            };
            runnable.runTaskTimer(Main.get(), 0, 20);
        }
    }
}
