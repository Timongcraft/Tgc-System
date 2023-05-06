package timongcraft.commands;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.executors.PlayerCommandExecutor;
import org.bukkit.entity.Player;
import timongcraft.Main;

public class ResourcePackCommand {
    public static void register() {
        new CommandTree("resourcepack")
                .withRequirement(sender -> (Main.get().getConfig().isSet("resourcePack.url") && !Main.get().getConfig().getString("resourcePack.url").isEmpty()) && (Main.get().getConfig().isSet("resourcePack.hash") && !Main.get().getConfig().getString("resourcePack.hash").isEmpty()))
                .withPermission("tgc-system.team")
                .executesPlayer(new ResourcePackExecutor())
                .register();
    }

    private static class ResourcePackExecutor implements PlayerCommandExecutor {
        @Override
        public void run(Player player, CommandArguments args) throws WrapperCommandSyntaxException {
            if(!Main.get().getDataConfig().isSet("players." + player.getUniqueId() + ".resourcepack")) {
                Main.get().getDataConfig().set("players." + player.getUniqueId() + ".resourcepack", true);
            }

            if(Main.get().getDataConfig().getBoolean("players." + player.getUniqueId() + ".resourcepack")) {
                Main.get().getDataConfig().set("players." + player.getUniqueId() + ".resourcepack", false);
                player.sendMessage(Main.get().getPrefix() + "Disabled resource pack request");
            } else {
                Main.get().getDataConfig().set("players." + player.getUniqueId() + ".resourcepack", true);
                player.sendMessage(Main.get().getPrefix() + "Enabled resource pack request");
            }
            Main.get().getDataConfig().save();
        }

    }

}
