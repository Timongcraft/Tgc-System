package timongcraft.system.commands;

import dev.jorel.commandapi.CommandAPIBukkit;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class SayCommand {

    public static void register() {
        CommandAPIBukkit.unregister("say", true, false);

        new CommandTree("say")
                .withShortDescription("Send a message from that sender")
                .withUsage("/say <message>")
                .withPermission(CommandPermission.OP)
                .then(new GreedyStringArgument("message")
                        .executes(SayCommand::sayManager))
                .register();
    }

    private static void sayManager(CommandSender sender, CommandArguments args) {
        TranslatableComponent message = new TranslatableComponent("chat.type.announcement", sender.getName(), args.get("message"));

        Bukkit.spigot().broadcast(message);
    }

}