package timongcraft.system.commands;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import timongcraft.system.Main;
import timongcraft.system.util.StatusHandler;

public class StatusCommand {
    public static void register() {
        new CommandTree("status")
                .withShortDescription("Set a status for yourself in the chat and tab list")
                .withUsage("/status [<status>]")
                .withRequirement(sender -> Main.get().getConfig().getBoolean("statuses.enabled"))
                .executesPlayer(StatusCommand::statusManager)
                .then(new GreedyStringArgument("status")
                        .setOptional(true)
                        .executesPlayer(StatusCommand::statusManager))
                .register();
    }

    private static int statusManager(Player sender, CommandArguments args) {
        String status = (String) args.get("status");

        if(args.args().length == 0) {
            if(StatusHandler.getStatus(sender) == null) {
                sender.sendMessage(Main.get().getPrefix() + "§cUsage: /status [<status>]");
                return 1;
            }

            StatusHandler.setStatus(sender, null);
            sender.sendMessage(Main.get().getPrefix() + "Reset your status");
        } else if(args.args().length == 1) {
            if(status.replaceAll("&[a-z-0-9]", "").length() > Main.get().getConfig().getInt("statuses.characterLimit")) {
                sender.sendMessage(Main.get().getPrefix() + "§cStatus can only be up to " + Main.get().getConfig().getInt("statuses.characterLimit") + " characters long!");
                return 1;
            }

            StatusHandler.setStatus(sender, status.replaceAll("&", "§"));
            sender.sendMessage(Main.get().getPrefix() + "Set your status to: " + status);
            return 1;
        }
        return 0;
    }
}