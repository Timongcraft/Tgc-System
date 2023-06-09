package timongcraft.system.commands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.executors.CommandExecutor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MeCommand {
    public static void register() {
        CommandAPI.unregister("me", true);

        new CommandTree("me")
                .withShortDescription("Send a me message from that sender")
                .withPermission(CommandPermission.OP)
                .then(new GreedyStringArgument("message")
                        .executes(new MeExecutor()))
                .register();
    }

    public static class MeExecutor implements CommandExecutor {
        @Override
        public void run(CommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException {
            String msg = (String) args.get("message");

            for(Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage("* " + sender.getName() + " " + msg);
            }
        }
    }
}
