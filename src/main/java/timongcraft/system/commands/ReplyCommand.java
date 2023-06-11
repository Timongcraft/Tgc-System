package timongcraft.system.commands;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
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
            return 1;
        }

        Player target = Bukkit.getPlayer(lastRepliedTo.get(sender.getUniqueId()));

        if (target == null) {
            sender.sendMessage(Main.get().getPrefix() + "§cThe player you last messaged is not online.");
            return 1;
        }

        if(sender.hasPermission("tgc-system.team")) {
            message = message.replaceAll("&", "§");
        }

        sender.sendMessage("§7§oYou whisper to " + "§7§o" + target.getName() + "§7§o: " + message);
        target.sendMessage("§7§o" + sender.getName() + "§7§o whispers to you§7§o: " + message);
        return 1;
    }

    public static void setLastReply(UUID senderUuid, UUID targetUuid) {
        lastRepliedTo.put(senderUuid, targetUuid);
        if(!lastRepliedTo.containsKey(targetUuid) || Bukkit.getPlayer(lastRepliedTo.get(targetUuid)) == null) {
            lastRepliedTo.put(targetUuid, senderUuid);
        }
    }
}