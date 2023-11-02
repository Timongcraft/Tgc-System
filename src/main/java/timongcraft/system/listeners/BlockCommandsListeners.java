package timongcraft.system.listeners;

import dev.jorel.commandapi.CommandAPIBukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import timongcraft.system.Main;

import java.util.Iterator;
import java.util.List;

public class BlockCommandsListeners implements Listener {

    public BlockCommandsListeners() {
        List<String> blockedCommands = Main.get().getConfig().getStringList("blockedCommands");

        for (String command : blockedCommands)
            CommandAPIBukkit.unregister(command, true, true);
    }

    @EventHandler
    public void blockPrefixes(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("tgc-system.team") || Main.get().getConfig().getStringList("blockedPrefixes").isEmpty())
            return;
        for (String blockedPrefixes : Main.get().getConfig().getStringList("blockedPrefixes")) {
            if (event.getMessage().toLowerCase().startsWith("/" + blockedPrefixes)) {
                event.setCancelled(true);
                player.sendMessage(Main.get().getPrefix() + "§cThe '" + blockedPrefixes + "' prefix is blocked!");
                break;
            }
        }
    }

    @EventHandler
    public void hidePrefixes(PlayerCommandSendEvent event) {
        Iterator<String> iterator = event.getCommands().iterator();
        if (event.getPlayer().hasPermission("tgc-system.team") || Main.get().getConfig().getStringList("blockedPrefixes").isEmpty())
            return;
        while (iterator.hasNext()) {
            String command = iterator.next();
            for (String blockedPrefixes : Main.get().getConfig().getStringList("blockedPrefixes")) {
                if (command.startsWith(blockedPrefixes)) {
                    iterator.remove();
                    break;
                }
            }
        }
    }

}
