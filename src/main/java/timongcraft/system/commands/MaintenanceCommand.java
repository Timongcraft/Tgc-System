package timongcraft.system.commands;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import timongcraft.system.Main;
import timongcraft.system.util.PlayerUtils;

import java.util.ArrayList;
import java.util.List;

public class MaintenanceCommand {
    public static void register() {
        new CommandTree("maintenance")
                .withShortDescription("A maintenance system")
                .withUsage(
                        "/maintenance <add|remove> [<target>]",
                        "/maintenance list"
                )
                .withPermission("tgc-system.team")
                .executes(MaintenanceCommand::maintenanceManager)
                .then(new LiteralArgument("add")
                        .then(new OfflinePlayerArgument("target")
                                .replaceSuggestions(ArgumentSuggestions.strings(info -> notMaintenanceList()))
                                .executes(MaintenanceCommand::maintenanceAddManager)))
                .then(new LiteralArgument("remove")
                        .then(new OfflinePlayerArgument("target")
                                .replaceSuggestions(ArgumentSuggestions.strings(info -> maintenanceList()))
                                .executes(MaintenanceCommand::maintenanceRemoveManager)))
                .then(new LiteralArgument("list")
                        .executes(MaintenanceCommand::maitnenanceListManager))
                .register();
    }

    private static String[] maintenanceList() {
        List<String> inMainenanceList = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (Main.get().getDataConfig().getBoolean("players." + player.getUniqueId() + ".maintenanceAllowed")) {
                inMainenanceList.add(player.getName());
            }
        }

        return inMainenanceList.toArray(new String[0]);
    }

    private static String[] notMaintenanceList() {
        List<String> notInMainenanceList = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!Main.get().getDataConfig().getBoolean("players." + player.getUniqueId() + ".maintenanceAllowed")) {
                notInMainenanceList.add(player.getName());
            }
        }

        return notInMainenanceList.toArray(new String[0]);
    }

    private static int maintenanceManager(CommandSender sender, CommandArguments args) {
        final String maintenanceKickMessage = Main.get().getConfig().getString("maintenance.kickMessage");

        boolean maintenanceMode = Main.get().getDataConfig().getBoolean("maintenance.enabled");
        maintenanceMode = !maintenanceMode;
        Main.get().getDataConfig().set("maintenance.enabled", maintenanceMode);
        Main.get().getDataConfig().save();

        if (maintenanceMode) {
            sender.sendMessage(Main.get().getPrefix() + "Maintenance mode enabled.");
            PlayerUtils.sendToTeam(sender.getName(), null, "Enabled maintenance mode");
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!isAllowed(player)) {
                    player.kickPlayer(maintenanceKickMessage);
                }
            }
        } else {
            sender.sendMessage(Main.get().getPrefix() + "Maintenance mode disabled.");
            PlayerUtils.sendToTeam(sender.getName(), null, "Disabled maintenance mode");
        }
        return 1;
    }

    private static int maitnenanceListManager(CommandSender sender, CommandArguments args) {
        sender.sendMessage(Main.get().getPrefix() + "Allowed players:");

        for (String uuid : Main.get().getDataConfig().getConfigurationSection("players").getKeys(false)) {
            boolean maintenanceAllowed = Main.get().getDataConfig().getBoolean("players." + uuid + ".maintenanceAllowed");
            if (maintenanceAllowed) {
                String playerName = Main.get().getDataConfig().getString("players." + uuid + ".name");
                sender.sendMessage(playerName);
            }
        }
        return 1;
    }

    private static int maintenanceAddManager(CommandSender sender, CommandArguments args) {
        OfflinePlayer target = (OfflinePlayer) args.get("target");
        if (isAllowed(target)) {
            sender.sendMessage(Main.get().getPrefix() + target.getName() + " is already on the maintenance list");
            return 1;
        }

        Main.get().getDataConfig().set("players." + target.getUniqueId() + ".maintenanceAllowed", true);
        Main.get().getDataConfig().save();

        sender.sendMessage(Main.get().getPrefix() + target.getName() + " has been added to the maintenance list");
        return 1;
    }

    private static int maintenanceRemoveManager(CommandSender sender, CommandArguments args) {
        OfflinePlayer target = (OfflinePlayer) args.get("target");
        if (!isAllowed(target)) {
            sender.sendMessage(Main.get().getPrefix() + target.getName() + " isn't on the maintenance list");
            return 1;
        }

        Main.get().getDataConfig().set("players." + target.getUniqueId() + ".maintenanceAllowed", false);
        Main.get().getDataConfig().save();

        sender.sendMessage(Main.get().getPrefix() + target.getName() + " has been removed from the maintenance list");
        return 1;
    }

    public static boolean isAllowed(OfflinePlayer player) {
        return Main.get().getDataConfig().getBoolean("players." + player.getUniqueId() + ".maintenanceAllowed");
    }

}
