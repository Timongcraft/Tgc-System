package timongcraft.commands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.executors.CommandExecutor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SayCommand {
    public static void register() {
        CommandAPI.unregister("say", true);

        new CommandTree("say")
                .withFullDescription("Send a message from that sender")
                .then(new GreedyStringArgument("message")
                        .executes(new SayExecutor()))
                .register();
    }

    public static class SayExecutor implements CommandExecutor {
        @Override
        public void run(CommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException {
            String msg = (String) args.get("message");

            for(Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage("[" + sender.getName() + "] " + msg);
            }
        }
    }
}