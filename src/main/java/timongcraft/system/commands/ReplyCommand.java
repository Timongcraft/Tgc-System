package timongcraft.system.commands;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import timongcraft.system.Main;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReplyCommand {

    private static final Map<UUID, UUID> lastRepliedTo = new HashMap<>();

    public static void register() {
        new CommandTree("reply")
                .withShortDescription("Reply to a private message")
                .withUsage("/reply <message>")
                .withAliases("r")
                .then(new GreedyStringArgument("message")
                        .executesPlayer(ReplyCommand::replyManager))
                .register();
    }

    private static int replyManager(Player sender, CommandArguments args) {
        String message = (String) args.get("message");

        if (!lastRepliedTo.containsKey(sender.getUniqueId())) {
            sender.sendMessage(Main.get().getPrefix() + "§cYou have no one to reply to.");
            return 0;
        }

        Player target = Bukkit.getPlayer(lastRepliedTo.get(sender.getUniqueId()));

        if (target == null) {
            sender.sendMessage(Main.get().getPrefix() + "§cThe player you last messaged is not online.");
            return 0;
        }

        if (sender.hasPermission("tgc-system.team"))
            message = message.replaceAll("&", "§");

        sender.spigot().sendMessage(new ComponentBuilder(new TranslatableComponent("commands.message.display.outgoing", target.getName(), message)).color(ChatColor.GRAY).italic(true).create());
        target.spigot().sendMessage(new ComponentBuilder(new TranslatableComponent("commands.message.display.incoming", sender.getName(), message)).color(ChatColor.GRAY).italic(true).create());
        return 1;
    }

    public static void setLastReply(UUID senderUuid, UUID targetUuid) {
        lastRepliedTo.put(senderUuid, targetUuid);
        if (!lastRepliedTo.containsKey(targetUuid) || Bukkit.getPlayer(lastRepliedTo.get(targetUuid)) == null)
            lastRepliedTo.put(targetUuid, senderUuid);
    }

}