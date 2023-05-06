package timongcraft.commands;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.executors.PlayerCommandExecutor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import timongcraft.Main;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReplyCommand {
    private static final Map<UUID, UUID> lastRepliedTo = new HashMap<>();

    public static void register() {
        new CommandTree("reply")
                .withFullDescription("Send a private message to the player you last sent a private message too")
                .withAliases("r")
                .then(new GreedyStringArgument("message")
                        .executesPlayer(new PlayerMsgExecutor()))
                .register();
    }

    private static class PlayerMsgExecutor implements PlayerCommandExecutor {
        @Override
        public void run(Player player, CommandArguments args) throws WrapperCommandSyntaxException {
            String msg = (String) args.get("message");

            if (!lastRepliedTo.containsKey(player.getUniqueId())) {
                player.sendMessage(Main.get().getPrefix() + "§cYou have no one to reply to.");
                return;
            }

            Player target = Bukkit.getPlayer(lastRepliedTo.get(player.getUniqueId()));

            if (target == null) {
                player.sendMessage(Main.get().getPrefix() + "§cThe player you last messaged is not online.");
                return;
            }

            if(player.hasPermission("tgc-system.team")) {
                msg = msg.replaceAll("&", "§");
            }

            player.sendMessage("§7§oYou whisper to " + "§7§o" + target.getName() + "§7§o: " + msg);
            target.sendMessage("§7§o" + player.getName() + "§7§o whispers to you§7§o: " + msg);
        }
    }

    public static void setLastReply(UUID senderUuid, UUID targetUuid) {
        lastRepliedTo.put(senderUuid, targetUuid);
        if(!lastRepliedTo.containsKey(targetUuid) || Bukkit.getPlayer(lastRepliedTo.get(targetUuid)) == null) {
            lastRepliedTo.put(targetUuid, senderUuid);
        }
    }
}