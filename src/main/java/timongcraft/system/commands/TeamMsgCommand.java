package timongcraft.system.commands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.executors.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import timongcraft.system.Main;
import timongcraft.system.util.PlayerUtils;

public class TeamMsgCommand {
    public static void register() {
        CommandAPI.unregister("teammsg", true);
        CommandAPI.unregister("tm", true);

        new CommandTree("teammsg")
                .withShortDescription("Send a private message to your team")
                .withUsage("/teammsg <message>")
                .then(new GreedyStringArgument("message")
                        .executes(TeamMsgCommand::teamMsgManager))
                .register();
    }

    private static int teamMsgManager(CommandSender sender, CommandArguments args) {
        String message = (String) args.get("message");

        if (!(sender instanceof Player target)) {
            sender.sendMessage(Main.get().getPrefix() + "§cAn entity is required to run this command here");
            return 1;
        }

        if (target.hasPermission("tgc-system.team")) {
            message = message.replaceAll("&", "§");
        }

        Team targetTeam = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(target.getName());

        if (targetTeam == null) {
            target.sendMessage(Main.get().getPrefix() + "§cYou must be on a team to message your team");
            return 1;
        }

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            for (Team onlinePlayersTeam : onlinePlayer.getScoreboard().getTeams()) {
                if (onlinePlayersTeam.hasEntry(onlinePlayer.getName()) && targetTeam.equals(onlinePlayersTeam)) {
                    onlinePlayer.sendMessage("-> " + targetTeam.getColor() + "[" + targetTeam.getDisplayName() + "] §r<" + new PlayerUtils().getPlayerNameWithStatus(target, true) + "§r" + "> " + message);
                }
            }
        }
        return 1;
    }
}