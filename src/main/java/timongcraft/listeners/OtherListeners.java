package timongcraft.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import timongcraft.Main;
import timongcraft.util.StatusHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OtherListeners implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if(event.isCancelled()) return;
        if(!Main.get().getConfig().getBoolean("chatSystem.enabled")) return;

        Player player = event.getPlayer();
        String playerChatName;
        String message = event.getMessage();

        if(Main.get().getConfig().getBoolean("statuses.enabled")) {
            if(player.hasPermission("tgc-system.team")) {
                playerChatName = (new StatusHandler().getStatus(player) != null ? new StatusHandler().getStatusWithBrackets(player) : "") + "§4<§c" + player.getName() + "§4>§f";
            } else playerChatName = (new StatusHandler().getStatus(player) != null ? new StatusHandler().getStatusWithBrackets(player) : "") + "§8<§7" + player.getName() + "§8>§7";
        } else {
            if(player.hasPermission("tgc-system.team")) {
                playerChatName = "§4<§c" + player.getName() + "§4>§f";
                message = message.replaceAll("&", "§");
            } else playerChatName = "§8<§7" + player.getName() + "§8>§7";
        }

        if(Main.get().getConfig().getBoolean("chatSystem.noLinks") && !player.hasPermission("sb.team")) {
            Pattern urlPattern = Pattern.compile("^(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?$");
            for (String messagePart : message.split("\\s+")) {
                Matcher matcher = urlPattern.matcher(messagePart);
                if (matcher.matches()) {
                    player.sendMessage(Main.get().getPrefix() + "§cYou don't have the permission to send links!");
                    event.setCancelled(true);
                    return;
                }
            }
        }

        final String[] highlightedMessage = {message};
        for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if(highlightedMessage[0].contains(onlinePlayer.getName())) {
                ChatColor highlightColor = (onlinePlayer.hasPermission("tgc-system.team") && (player.hasPermission("tgc-system.team"))) ? ChatColor.YELLOW : ChatColor.GRAY;
                highlightedMessage[0] = highlightedMessage[0].replaceAll(onlinePlayer.getName(), highlightColor + onlinePlayer.getName() + ChatColor.RESET);

                if(highlightColor == ChatColor.YELLOW) {
                    onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1.0F, 2.0F);
                }
            }

            onlinePlayer.sendMessage(null, (Main.get().getConfig().getBoolean("chatSystem.timeStampInChat.enabled") ? "§7[" + DateTimeFormatter.ofPattern(Main.get().getConfig().getString("chatSystem.timeStampInChat.format")).format(ZonedDateTime.now(ZoneId.of(Main.get().getConfig().getString("chatSystem.timeStampInChat.timeZone")))) + "] §f" : "") + playerChatName + " " + highlightedMessage[0]);
            Bukkit.getLogger().info("Chat: " + playerChatName + " " + highlightedMessage[0]);
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerResourcePackStatus(PlayerResourcePackStatusEvent event) {
        Player player = event.getPlayer();
        if(!Main.get().getConfig().getBoolean("resourcePack.force")) return;
        if(player.hasPermission("tgc-system.team")) return;
        PlayerResourcePackStatusEvent.Status status = event.getStatus();

        if(status == PlayerResourcePackStatusEvent.Status.DECLINED || status == PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD) {
            player.kickPlayer("You must use the server's resource pack. Without it the game doesn't work.");
        }
    }

}