package timongcraft.system.commands;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import timongcraft.system.Main;

import java.util.HexFormat;

public class ResourcePackCommand {

    public static void register() {
        new CommandTree("resourcepack")
                .withShortDescription("Toggle the server resource pack")
                .withUsage("/resourcepack")
                .withRequirement(sender -> (Main.get().getConfig().isSet("resourcePack.url") && !Main.get().getConfig().getString("resourcePack.url").isEmpty()) && (Main.get().getConfig().isSet("resourcePack.hash") && !Main.get().getConfig().getString("resourcePack.hash").isEmpty()))
                .withPermission("tgc-system.team")
                .executesPlayer(ResourcePackCommand::resourcePackManager)
                .register();
    }

    private static void resourcePackManager(Player sender, CommandArguments args) {
        if (!Main.get().getDataConfig().isSet("players." + sender.getUniqueId() + ".resourcepack"))
            Main.get().getDataConfig().set("players." + sender.getUniqueId() + ".resourcepack", false);

        if (Main.get().getDataConfig().getBoolean("players." + sender.getUniqueId() + ".resourcepack")) {
            Main.get().getDataConfig().set("players." + sender.getUniqueId() + ".resourcepack", false);
            sender.sendMessage(Main.get().getPrefix() + "Disabled resource pack request");
        } else {
            Main.get().getDataConfig().set("players." + sender.getUniqueId() + ".resourcepack", true);
            sender.sendMessage(Main.get().getPrefix() + "Enabled resource pack request");

            final String url = Main.get().getConfig().getString("resourcePack.url");
            final byte[] hash = HexFormat.of().parseHex(Main.get().getConfig().getString("resourcePack.hash"));

            if (url != null && hash != null && sender.hasPermission("tgc-system.team") && Main.get().getDataConfig().getBoolean("players." + sender.getUniqueId() + ".resourcepack"))
                sender.setResourcePack(url, hash, false);
        }
        Main.get().getDataConfig().save();
    }

}
