package timongcraft.system.commands;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import timongcraft.system.Main;
import timongcraft.system.util.PlayerUtils;

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
        sender.sendMessage(Main.get().getPrefix() + "The Configs have been reloaded!");
        PlayerUtils.sendToTeam(sender.getName(), null, "Reloaded the plugins's configs");
        return 1;
    }
}