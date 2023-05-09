package timongcraft.commands;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.executors.CommandExecutor;
import org.bukkit.command.CommandSender;
import timongcraft.Main;
import timongcraft.util.TeamUtils;

public class ReloadConfigsCommand {
    public static void register() {
        new CommandTree("tgcreload-configs")
                .withFullDescription("Reloads the plugin config and the data config")
                .withPermission("tgc-system.team")
                .executes(new ReloadConfigsExecutor())
                .register();
    }

    public static class ReloadConfigsExecutor implements CommandExecutor {
        @Override
        public void run(CommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException {
            Main.get().reloadConfig();
            Main.get().getDataConfig().load();
            sender.sendMessage(Main.get().getPrefix() + "The Configs have been reloaded!");
            TeamUtils.sendToTeam(sender.getName(), null, "Reloaded the plugins's configs");
        }
    }
}