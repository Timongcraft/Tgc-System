package timongcraft.commands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.executors.CommandBlockCommandExecutor;
import dev.jorel.commandapi.executors.ConsoleCommandExecutor;
import dev.jorel.commandapi.executors.PlayerCommandExecutor;
import org.bukkit.Bukkit;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class MsgCommand {
    public static void register() {
        CommandAPI.unregister("msg", true);
        CommandAPI.unregister("minecraft:msg", true);
        CommandAPI.unregister("tell", true);
        CommandAPI.unregister("minecraft:tell", true);
        CommandAPI.unregister("w", true);
        CommandAPI.unregister("minecraft:w", true);

        new CommandTree("msg")
                .withFullDescription("Send a private message to a player")
                .withAliases("tell", "w")
                .then(new EntitySelectorArgument.OnePlayer("target").replaceSuggestions(ArgumentSuggestions.strings(info -> Bukkit.getOnlinePlayers().stream()
                                .map(Player::getName)
                                .toArray(String[]::new)))
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
            String msg = (String) args.get("message");

            if(player.hasPermission("tgc-system.team")) {
                msg = msg.replaceAll("&", "§");
            }

            player.sendMessage("§7§oYou whisper to " + "§7§o" + target.getName() + "§7§o: " + msg);
            target.sendMessage("§7§o" + player.getName() + "§7§o whispers to you§7§o: " + msg);
            ReplyCommand.setLastReply(player.getUniqueId(), target.getUniqueId());
        }
    }

    private static  class ConsoleMsgExecutor implements ConsoleCommandExecutor {
        @Override
        public void run(ConsoleCommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException {
            Player target = (Player) args.get("target");
            String msg = (String) args.get("message");
            msg = msg.replaceAll("&", "§");

            sender.sendMessage("§7§oYou whisper to " + "§7§o" + target.getName() + "§7§o: " + msg);
            target.sendMessage("§7§oServer whispers to you§7§o: " + msg);
        }
    }

    private static  class CommandBlockMsgExecutor implements CommandBlockCommandExecutor {
        @Override
        public void run(BlockCommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException {
            Player target = (Player) args.get("target");
            String msg = (String) args.get("message");
            msg = msg.replaceAll("&", "§");

            sender.sendMessage("§7§oYou whisper to " + "§7§o" + target.getName() + "§7§o: " + msg);
            target.sendMessage("§7§o@ whispers to you§7§o: " + msg);
        }
    }
}