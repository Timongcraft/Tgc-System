package timongcraft.system.commands;

import dev.jorel.commandapi.CommandAPIBukkit;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class MeCommand {

    public static void register() {
        CommandAPIBukkit.unregister("me", true, false);

        new CommandTree("me")
                .withShortDescription("Send a me message from that sender")
                .withUsage("/me <message>")
                .withPermission(CommandPermission.OP)
                .then(new GreedyStringArgument("message")
                        .executes(MeCommand::meManager))
                .register();
    }

    private static void meManager(CommandSender sender, CommandArguments args) {
        String msg = (String) args.get("message");

        Bukkit.broadcastMessage("* " + sender.getName() + " " + msg);
    }

}
