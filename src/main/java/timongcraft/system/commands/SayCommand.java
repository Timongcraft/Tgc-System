package timongcraft.system.commands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SayCommand {
    public static void register() {
        CommandAPI.unregister("say", true);

        new CommandTree("say")
                .withShortDescription("Send a message from that sender")
                .withUsage("/say <message>")
                .withPermission(CommandPermission.OP)
                .then(new GreedyStringArgument("message")
                        .executes(SayCommand::sayManager))
                .register();
    }

    private static int sayManager(CommandSender sender, CommandArguments args) {
        String msg = (String) args.get("message");

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage("[" + sender.getName() + "] " + msg);
        }
        return 1;
    }
}