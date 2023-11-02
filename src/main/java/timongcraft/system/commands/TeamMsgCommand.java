package timongcraft.system.commands;

import dev.jorel.commandapi.CommandAPIBukkit;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import timongcraft.system.Main;
import timongcraft.system.util.MessageUtils;

public class TeamMsgCommand {

    public static void register() {
        CommandAPIBukkit.unregister("teammsg", true, false);
        CommandAPIBukkit.unregister("tm", true, false);

        new CommandTree("teammsg")
                .withShortDescription("Send a private message to your team")
                .withUsage("/teammsg <message>")
                .withAliases("tm")
                .then(new GreedyStringArgument("message")
                        .executesPlayer(TeamMsgCommand::teamMsgManager))
                .register();
    }

    private static int teamMsgManager(Player sender, CommandArguments args) {
        String message = (String) args.get("message");

        if (sender.hasPermission("tgc-system.team"))
            message = message.replaceAll("&", "§");

        Team targetTeam = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(sender.getName());

        if (targetTeam == null) {
            sender.sendMessage(Main.get().getPrefix() + "§cYou must be on a team to message your team");
            return 0;
        }

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            for (Team onlinePlayersTeam : onlinePlayer.getScoreboard().getTeams()) {
                if (!onlinePlayersTeam.hasEntry(onlinePlayer.getName()) || !targetTeam.equals(onlinePlayersTeam))
                    continue;
                onlinePlayer.sendMessage("-> " + targetTeam.getColor() + "[" + targetTeam.getDisplayName() + "] §r<" + MessageUtils.getPlayerNameWithStatus(sender, true) + "§r" + "> " + message);
            }
        }
        return 1;
    }

}