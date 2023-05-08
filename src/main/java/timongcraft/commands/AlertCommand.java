package timongcraft.commands;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.executors.CommandExecutor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import timongcraft.Main;

public class AlertCommand {
    public static void register() {
        new CommandTree("alert")
                .withFullDescription("Sends an alert to all players")
                .withAliases("broadcast")
                .withPermission("tgc-system.team")
                .then(new GreedyStringArgument("message")
                        .executes(new AlertExecutor()))
                .register();
    }

    private static class AlertExecutor implements CommandExecutor {
        @Override
        public void run(CommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException {
            String message = (String) args.get("message");

            for(Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(Main.get().getConfig().getString("prefix.alertPrefix") + message.replaceAll("&", "§"));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F,.5F);
            }
        }
    }
}
