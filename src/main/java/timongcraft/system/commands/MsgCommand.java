package timongcraft.system.commands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
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
                .withAliases("tell", "w")
                .then(new PlayerOnlyArgument("target")
                        .then(new GreedyStringArgument("message")
                                .executesPlayer(new PlayerMsgExecutor())
                                .executesCommandBlock(new CommandBlockMsgExecutor())
                                .executesConsole(new ConsoleMsgExecutor())))
                .register();
    }

    private static class PlayerMsgExecutor implements PlayerCommandExecutor {
        @Override
        public void run(Player player, CommandArguments args) throws WrapperCommandSyntaxException {
            Player target = (Player) args.get("target");
            String message = (String) args.get("message");

            if(player.hasPermission("tgc-system.team")) {
                message = message.replaceAll("&", "§");
            }

            player.sendMessage("§7§oYou whisper to " + target.getName() + ": " + message);
            target.sendMessage("§7§o" + player.getName() + " whispers to you: " + message);
            ReplyCommand.setLastReply(player.getUniqueId(), target.getUniqueId());
        }
    }

    private static class ConsoleMsgExecutor implements ConsoleCommandExecutor {
        @Override
        public void run(ConsoleCommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException {
            Player target = (Player) args.get("target");
            String message = (String) args.get("message");
            message = message.replaceAll("&", "§");

            sender.sendMessage("§7§oYou whisper to " + target.getName() + ": " + message);
            target.sendMessage("§7§oServer whispers to you: " + message);
        }
    }

    private static class CommandBlockMsgExecutor implements CommandBlockCommandExecutor {
        @Override
        public void run(BlockCommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException {
            Player target = (Player) args.get("target");
            String message = (String) args.get("message");
            message = message.replaceAll("&", "§");

            sender.sendMessage("§7§oYou whisper to " + target.getName() + message);
            target.sendMessage("§7§o@ whispers to you: " + message);
        }
    }
}