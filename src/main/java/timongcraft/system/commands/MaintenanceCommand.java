package timongcraft.system.commands;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import timongcraft.system.Main;
import timongcraft.system.util.MessageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
                        .executes(MaintenanceCommand::maintenanceListManager))
                .register();
    }

    private static String[] maintenanceList() {
        List<String> inMainenanceList = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers())
            if (isAllowed(player.getUniqueId()))
                inMainenanceList.add(player.getName());

        return inMainenanceList.toArray(new String[0]);
    }

    private static String[] notMaintenanceList() {
        List<String> notInMainenanceList = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers())
            if (!isAllowed(player.getUniqueId()))
                notInMainenanceList.add(player.getName());

        return notInMainenanceList.toArray(new String[0]);
    }

    private static void maintenanceManager(CommandSender sender, CommandArguments args) {
        final String maintenanceKickMessage = Main.get().getConfig().getString("maintenance.kickMessage");

        boolean maintenanceMode = Main.get().getDataConfig().getBoolean("maintenance.enabled");
        maintenanceMode = !maintenanceMode;
        Main.get().getDataConfig().set("maintenance.enabled", maintenanceMode);
        Main.get().getDataConfig().save();

        MessageUtils.sendAdminMessage(sender, new TextComponent("Maintenance mode set to " + maintenanceMode));
        if (maintenanceMode)
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (isAllowed(player.getUniqueId())) continue;
                player.kickPlayer(maintenanceKickMessage);
            }
    }

    private static void maintenanceListManager(CommandSender sender, CommandArguments args) {
        sender.sendMessage(Main.get().getPrefix() + "Allowed players:");
        for (String uniqueId : Main.get().getDataConfig().getConfigurationSection("players").getKeys(false)) {
            if (!Main.get().getDataConfig().getBoolean("players." + uniqueId + ".maintenanceAllowed")) continue;
            sender.sendMessage(Main.get().getDataConfig().getString("players." + uniqueId + ".name"));
        }
    }

    private static int maintenanceAddManager(CommandSender sender, CommandArguments args) {
        OfflinePlayer target = (OfflinePlayer) args.get("target");

        if (isAllowed(target.getUniqueId())) {
            sender.sendMessage(Main.get().getPrefix() + "§c" + target.getName() + " is already on the maintenance list");
            return 0;
        }

        Main.get().getDataConfig().set("players." + target.getUniqueId() + ".maintenanceAllowed", true);
        Main.get().getDataConfig().save();

        sender.sendMessage(Main.get().getPrefix() + target.getName() + " has been added to the maintenance list");
        return 1;
    }

    private static int maintenanceRemoveManager(CommandSender sender, CommandArguments args) {
        OfflinePlayer target = (OfflinePlayer) args.get("target");

        if (!isAllowed(target.getUniqueId())) {
            sender.sendMessage(Main.get().getPrefix() + "§c" + target.getName() + " isn't on the maintenance list");
            return 0;
        }

        Main.get().getDataConfig().set("players." + target.getUniqueId() + ".maintenanceAllowed", false);
        Main.get().getDataConfig().save();

        sender.sendMessage(Main.get().getPrefix() + target.getName() + " has been removed from the maintenance list");
        return 1;
    }

    public static boolean isAllowed(UUID playerUniqueId) {
        return Main.get().getDataConfig().getBoolean("players." + playerUniqueId + ".maintenanceAllowed");
    }

}
