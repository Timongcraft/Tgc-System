package timongcraft.commands;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.executors.PlayerCommandExecutor;
import org.bukkit.entity.Player;
import timongcraft.Main;
import timongcraft.util.StatusHandler;

public class StatusCommand {
    public static void register() {
        new CommandTree("status")
                .withFullDescription("Set a status for yourself in the chat and tab list")
                .withRequirement(sender -> Main.get().getConfig().getBoolean("statuses.enabled"))
                .executesPlayer(new StatusExecutor())
                .then(new GreedyStringArgument("status")
                        .setOptional(true)
                        .executesPlayer(new StatusExecutor()))
                .register();
    }

    private static class StatusExecutor implements PlayerCommandExecutor {
        @Override
        public void run(Player player, CommandArguments args) throws WrapperCommandSyntaxException {
            String status = (String) args.get("status");

            if(args.args().length == 0) {
                if(StatusHandler.getStatus(player) == null) {
                    player.sendMessage(Main.get().getPrefix() + "§cUsage: /status <status>");
                    return;
                }

                StatusHandler.setStatus(player, null);
                player.sendMessage(Main.get().getPrefix() + "§aSuccessfully reset your status");
            } else if(args.args().length == 1) {
                if(status.replaceAll("&[a-z-0-9]", "").length() > Main.get().getConfig().getInt("statuses.characterLimit")) {
                    player.sendMessage(Main.get().getPrefix() + "§cStatus can only be up to " + Main.get().getConfig().getInt("statuses.characterLimit") + " characters long!");
                    return;
                }

                StatusHandler.setStatus(player, status.replaceAll("&", "§"));
                player.sendMessage(Main.get().getPrefix() + "§aSuccessfully set your status to: " + status);
            }
        }
    }
}