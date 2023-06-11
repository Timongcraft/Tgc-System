package timongcraft.system.commands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.executors.*;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import timongcraft.system.util.PlayerOnlyArgument;

public class MsgCommand {
    public static void register() {
        CommandAPI.unregister("msg", true);
        CommandAPI.unregister("minecraft:msg", true);
        CommandAPI.unregister("tell", true);
        CommandAPI.unregister("minecraft:tell", true);
        CommandAPI.unregister("w", true);
        CommandAPI.unregister("minecraft:w", true);

        new CommandTree("msg")
                .withShortDescription("Send a private message to a player")
                .withUsage("/msg <target> <message>")
                .withAliases("tell", "w")
                .then(new PlayerOnlyArgument("target")
                        .then(new GreedyStringArgument("message")
                                .executesPlayer(MsgCommand::playerMsgManager)
                                .executesCommandBlock(MsgCommand::commandBlockManager)
                                .executesConsole(MsgCommand::consoleMsgManager)))
                .register();
    }

    private static int playerMsgManager(Player sender, CommandArguments args) {
        Player target = (Player) args.get("target");
        String message = (String) args.get("message");

        if(sender.hasPermission("tgc-system.team")) {
            message = message.replaceAll("&", "§");
        }

        sender.sendMessage("§7§oYou whisper to " + target.getName() + ": " + message);
        target.sendMessage("§7§o" + sender.getName() + " whispers to you: " + message);
        ReplyCommand.setLastReply(sender.getUniqueId(), target.getUniqueId());
        return 1;
    }

    private static int consoleMsgManager(ConsoleCommandSender sender, CommandArguments args) {
        Player target = (Player) args.get("target");
        String message = (String) args.get("message");
        message = message.replaceAll("&", "§");

        sender.sendMessage("§7§oYou whisper to " + target.getName() + ": " + message);
        target.sendMessage("§7§oServer whispers to you: " + message);
        return 1;
    }

    private static int commandBlockManager(BlockCommandSender sender, CommandArguments args) {
        Player target = (Player) args.get("target");
        String message = (String) args.get("message");
        message = message.replaceAll("&", "§");

        sender.sendMessage("§7§oYou whisper to " + target.getName() + message);
        target.sendMessage("§7§o@ whispers to you: " + message);
        return 1;
    }
}