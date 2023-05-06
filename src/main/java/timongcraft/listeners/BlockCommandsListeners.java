package timongcraft.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import timongcraft.Main;

import java.util.Iterator;

public class BlockCommandsListeners implements Listener {

    @EventHandler
    public void blockPrefixes(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if(player.hasPermission("tgc-system.team") || Main.get().getConfig().getStringList("blockedPrefixes").isEmpty()) return;
        for (String blockedPrefixes : Main.get().getConfig().getStringList("blockedPrefixes")) {
            if(event.getMessage().toLowerCase().startsWith("/" + blockedPrefixes)) {
                event.setCancelled(true);
                player.sendMessage(Main.get().getPrefix() + "§cThe '" + blockedPrefixes + "' prefix is blocked!");
                break;
            }
        }
    }

    @EventHandler
    public void hidePrefixes(PlayerCommandSendEvent event) {
        Iterator<String> iterator = event.getCommands().iterator();
        if(event.getPlayer().hasPermission("tgc-system.team") || Main.get().getConfig().getStringList("blockedPrefixes").isEmpty()) return;
        while (iterator.hasNext()) {
            String command = iterator.next();
            for (String blockedPrefixes : Main.get().getConfig().getStringList("blockedPrefixes")) {
                if(command.startsWith(blockedPrefixes)) {
                    iterator.remove();
                    break;
                }
            }
        }
    }

    @EventHandler
    public void blockCommands(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if(player.hasPermission("tgc-system.team") || Main.get().getConfig().getStringList("blockedCommands").isEmpty()) return;
        String command = event.getMessage().substring(1).toLowerCase().split("\\s+")[0];
        if(!Main.get().getConfig().getStringList("blockedCommands").contains(command)) return;
        player.sendMessage(Main.get().getPrefix() + "§cThe '" + command + "' command is blocked!");
        event.setCancelled(true);
    }

    @EventHandler
    public void hideCommands(PlayerCommandSendEvent event) {
        if(event.getPlayer().hasPermission("tgc-system.team") || Main.get().getConfig().getStringList("blockedCommands").isEmpty()) return;
        event.getCommands().removeIf(command -> Main.get().getConfig().getStringList("blockedCommands").contains(command));
    }
}
