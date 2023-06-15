package timongcraft.system.commands;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import timongcraft.system.Main;

public class AlertCommand {
    public static void register() {
        new CommandTree("alert")
                .withShortDescription("Sends an alert to all players")
                .withUsage("/alert <message>")
                .withAliases("broadcast")
                .withPermission("tgc-system.team")
                .then(new GreedyStringArgument("message")
                        .executes(AlertCommand::alertManager))
                .register();
    }

    private static int alertManager(CommandSender sender, CommandArguments args) {
        String message = (String) args.get("message");

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(Main.get().getConfig().getString("prefix.alertPrefix") + message.replaceAll("&", "§"));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, .5F);
        }
        return 1;
    }
}
