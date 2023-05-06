package timongcraft.commands;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.executors.CommandExecutor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import timongcraft.Main;
import timongcraft.util.TeamUtils;

import java.util.ArrayList;
import java.util.List;

public class MaintenanceCommand {
    public static void register() {
        new CommandTree("maintenance")
                .withFullDescription("A maintenance system")
                .withPermission("tgc-system.team")
                .executes(new MaintenanceExecutor())
                .then(new LiteralArgument("add")
                        .then(new EntitySelectorArgument.OnePlayer("target")
                                .replaceSuggestions(ArgumentSuggestions.strings(info -> Bukkit.getOnlinePlayers().stream()
                                        .map(Player::getName)
                                        .toArray(String[]::new)))
                                .executes(new MaintenanceAddExecutor())))
                .then(new LiteralArgument("remove")
                        .then(new EntitySelectorArgument.OnePlayer("target")
                                .replaceSuggestions(ArgumentSuggestions.strings(info -> MaintenanceList()))
                                .executes(new MaintenanceRemoveExecutor())))
                .then(new LiteralArgument("list")
                        .executes(new MaintenanceListExecutor()))
                .register();
    }

    private static String[] MaintenanceList() {
        List<String> inMainenanceList = new ArrayList<>();

        for(Player player : Bukkit.getOnlinePlayers()) {
            if(Main.get().getDataConfig().getBoolean("players." + player.getUniqueId() + ".maintenanceAllowed")) {
                inMainenanceList.add(player.getName());
            }
        }

        return inMainenanceList.toArray(new String[0]);
    }

    private static class MaintenanceExecutor implements CommandExecutor {
        @Override
        public void run(CommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException {
            final String maintenanceKickMessage = Main.get().getConfig().getString("maintenance.kickMessage");

            if(args.args().length == 0) {
                boolean maintenanceMode = Main.get().getDataConfig().getBoolean("maintenance.enabled");
                maintenanceMode = !maintenanceMode;
                Main.get().getDataConfig().set("maintenance.enabled", maintenanceMode);
                Main.get().getDataConfig().save();

                if(maintenanceMode) {
                    sender.sendMessage(Main.get().getPrefix() + "Maintenance mode enabled.");
                    TeamUtils.sendToTeam(sender.getName(), null, false, "Enabled maintenance mode");
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        if(!isAllowed(player)) {
                            player.kickPlayer(maintenanceKickMessage);
                        }
                    }
                } else {
                    sender.sendMessage(Main.get().getPrefix() + "Maintenance mode disabled.");
                    TeamUtils.sendToTeam(sender.getName(), null, false, "Disabled maintenance mode");
                }

            }
        }
    }

    private static class MaintenanceListExecutor implements CommandExecutor {
        @Override
        public void run(CommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException {
            sender.sendMessage(Main.get().getPrefix() + "Allowed players:");

            for (String uuid : Main.get().getDataConfig().getConfigurationSection("players").getKeys(false)) {
                boolean maintenanceAllowed = Main.get().getDataConfig().getBoolean("players." + uuid + ".maintenanceAllowed");
                if (!maintenanceAllowed) {
                    String playerName = Main.get().getDataConfig().getString("players." + uuid + ".name");
                    sender.sendMessage(playerName);
                }
            }
        }
    }

    private static class MaintenanceAddExecutor implements CommandExecutor {
        @Override
        public void run(CommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException {
            Player target = (Player) args.get("target");
            if(isAllowed(target)) {
                sender.sendMessage(Main.get().getPrefix() + target.getName() + " is already on the maintenance list");
                return;
            }

            Main.get().getDataConfig().set("players." + target.getUniqueId() + ".maintenanceAllowed", true);
            Main.get().getDataConfig().save();

            sender.sendMessage(Main.get().getPrefix() + target.getName() + " has been added to the maintenance list");
        }
    }

    private static class MaintenanceRemoveExecutor implements CommandExecutor {
        @Override
        public void run(CommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException {
            Player target = (Player) args.get("target");
            if(!isAllowed(target)) {
                sender.sendMessage(Main.get().getPrefix() + target.getName() + " isn't on the maintenance list");
                return;
            }

            Main.get().getDataConfig().set("players." + target.getUniqueId() + ".maintenanceAllowed", false);
            Main.get().getDataConfig().save();

            sender.sendMessage(Main.get().getPrefix() + target.getName() + " has been removed from the maintenance list");
        }
    }

    public static boolean isAllowed(Player player) {
        return Main.get().getDataConfig().getBoolean("players." + player.getUniqueId() + ".maintenanceAllowed");
    }

}
