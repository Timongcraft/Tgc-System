package timongcraft.system.util;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import timongcraft.system.Main;


public class MessageUtils {

    public static void sendAdminMessage(CommandSender sender, TextComponent message) {
        sendAdminMessage(sender, true, message);
    }

    public static void sendAdminMessage(CommandSender sender, boolean sendToSender, TextComponent message) {
        TranslatableComponent text = new TranslatableComponent("chat.type.admin", sender.getName(), message);
        text.setColor(ChatColor.GRAY);
        text.setItalic(true);

        for (Player adminPlayer : Bukkit.getOnlinePlayers()) {
            if (!adminPlayer.hasPermission("tgc-system.team")) continue;
            if (adminPlayer.equals(sender)) {
                if (sendToSender)
                    adminPlayer.spigot().sendMessage(new TextComponent(Main.get().getPrefix()), message);
                continue;
            }

            adminPlayer.spigot().sendMessage(text);
        }
    }

    public static void sendAdminMessage(String senderName, TextComponent message) {
        sendAdminMessage(senderName, true, message);
    }

    public static void sendAdminMessage(String senderName, boolean sendToSender, TextComponent message) {
        TranslatableComponent text = new TranslatableComponent("chat.type.admin", senderName, message);
        text.setColor(ChatColor.GRAY);
        text.setItalic(true);

        for (Player adminPlayer : Bukkit.getOnlinePlayers()) {
            if (!adminPlayer.hasPermission("tgc-system.team")) continue;
            if (adminPlayer.getName().equals(senderName) && sendToSender) {
                adminPlayer.spigot().sendMessage(new TextComponent(Main.get().getPrefix()), message);
                continue;
            }

            adminPlayer.spigot().sendMessage(text);
        }
    }

    public static String getPlayerNameWithStatus(Player player, boolean statusEnabled) {
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(player.getName());
        String teamPrefix = team != null ? team.getColor() + (!team.getPrefix().isEmpty() ? (team.getPrefix()) : "") : "";
        String teamSuffix = team != null ? team.getColor() + (!team.getSuffix().isEmpty() ? (team.getSuffix()) : "") : "";

        return (statusEnabled ? StatusHandler.getStatusWithBrackets(player) : "") + teamPrefix + player.getName() + teamSuffix;
    }
}
