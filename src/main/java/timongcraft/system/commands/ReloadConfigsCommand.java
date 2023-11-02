package timongcraft.system.commands;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.executors.CommandArguments;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import timongcraft.system.Main;
import timongcraft.system.util.MessageUtils;

public class ReloadConfigsCommand {

    public static void register() {
        new CommandTree("tgcreload-configs")
                .withShortDescription("Reloads the plugin configs")
                .withUsage("/tgcreload-configs")
                .withPermission("tgc-system.team")
                .executes(ReloadConfigsCommand::reloadConfigsManager)
                .register();
    }

    private static int reloadConfigsManager(CommandSender sender, CommandArguments args) {
        Main.get().reloadConfig();
        Main.get().getDataConfig().load();

        MessageUtils.sendAdminMessage(sender, new TextComponent("Reloaded the plugins's configs"));
        sender.sendMessage("§eNot all modules are compatible with config reloading!");
        return 1;
    }

}