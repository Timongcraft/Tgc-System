package timongcraft.system.commands;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.executors.CommandArguments;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import timongcraft.system.Main;
import timongcraft.system.util.MessageUtils;

import java.util.ArrayList;
import java.util.List;

public class PermissionManagerCommand {

    public static void register() {
        new CommandTree("permissionmanager")
                .withShortDescription("Set or unset groups for players")
                .withUsage(
                        "/permissionmanager player <target> permission set <permission> <value>",
                        "/permissionmanager player <target> permission unset <permission>",
                        "/permissionmanager player <target> group <set|unset> <group>",
                        "/permissionmanager group <group> set <permission> <value>",
                        "/permissionmanager group <group> unset <permission>"
                )
                .withPermission("tgc-system.team")
                .withAliases("pm")
                .then(new LiteralArgument("player")
                        .then(new OfflinePlayerArgument("target")
                                .replaceSuggestions(ArgumentSuggestions.strings(info -> Bukkit.getOnlinePlayers().stream()
                                        .map(OfflinePlayer::getName)
                                        .toArray(String[]::new)))
                                .then(new LiteralArgument("permission")
                                        .then(new LiteralArgument("set")
                                                .then(new StringArgument("permission")
                                                        .then(new BooleanArgument("value")
                                                                .executes(PermissionManagerCommand::permissionSetManager))))
                                        .then(new LiteralArgument("unset")
                                                .then(new StringArgument("permission")
                                                        .replaceSuggestions(ArgumentSuggestions.strings(info -> getPermissionsForPlayer((OfflinePlayer) info.previousArgs().get("target"))))
                                                        .executes(PermissionManagerCommand::permissionUnsetManager))))
                                .then(new LiteralArgument("group")
                                        .then(new MultiLiteralArgument("mode", "set", "unset")
                                                .then(new StringArgument("group")
                                                        .includeSuggestions(ArgumentSuggestions.strings(info -> getGroups()))
                                                        .executes(PermissionManagerCommand::playerGroupManager))))))
                .then(new LiteralArgument("group")
                        .then(new StringArgument("group")
                                .includeSuggestions(ArgumentSuggestions.strings(info -> getGroups()))
                                .then(new LiteralArgument("set")
                                        .then(new StringArgument("permission")
                                                .then(new BooleanArgument("value")
                                                        .executes(PermissionManagerCommand::groupSetPermissionManager))))
                                .then(new LiteralArgument("unset")
                                        .then(new StringArgument("permission")
                                                .replaceSuggestions(ArgumentSuggestions.strings(info -> getPermissionsForGroup((String) info.previousArgs().get("group"))))
                                                .executes(PermissionManagerCommand::groupUnsetPermissionManager)))))
                .register();
    }

    private static String[] getGroups() {
        ConfigurationSection groupsSection = Main.get().getDataConfig().getConfigurationSection("groups");
        if (groupsSection == null)
            return new String[0];

        return groupsSection.getKeys(false).toArray(new String[0]);
    }

    private static String[] getPermissionsForPlayer(OfflinePlayer playerName) {
        List<String> permissionNames = new ArrayList<>();

        for (String permissionString : Main.get().getDataConfig().getStringList("players." + playerName.getUniqueId() + ".permissions")) {
            String permissionName = permissionString.split(":")[0];
            permissionNames.add(permissionName);
        }

        return permissionNames.toArray(new String[0]);
    }

    private static String[] getPermissionsForGroup(String groupName) {
        List<String> permissionNames = new ArrayList<>();

        for (String permissionString : Main.get().getDataConfig().getStringList("groups." + groupName + ".permissions")) {
            String permissionName = permissionString.split(":")[0];
            permissionNames.add(permissionName);
        }

        return permissionNames.toArray(new String[0]);
    }

    private static void permissionSetManager(CommandSender sender, CommandArguments args) {
        OfflinePlayer targetPlayer = (OfflinePlayer) args.get("target");
        String permission = (String) args.get("permission");
        Boolean value = (Boolean) args.get("value");

        List<String> playerPermissions = Main.get().getDataConfig().getStringList("players." + targetPlayer.getUniqueId() + ".permissions");

        playerPermissions.removeIf(s -> s.split(":")[0].equals(permission));
        playerPermissions.add(permission + ":" + value);
        MessageUtils.sendAdminMessage(sender, new TextComponent("Set permission " + permission + " of " + targetPlayer.getName()));

        Main.get().getDataConfig().set("players." + targetPlayer.getUniqueId() + ".permissions", playerPermissions);
        Main.get().getDataConfig().save();
    }

    private static void permissionUnsetManager(CommandSender sender, CommandArguments args) {
        OfflinePlayer targetPlayer = (OfflinePlayer) args.get("target");
        String permission = (String) args.get("permission");

        List<String> playerPermissions = Main.get().getDataConfig().getStringList("players." + targetPlayer.getUniqueId() + ".permissions");
        if (playerPermissions.equals(new ArrayList<>())) playerPermissions = new ArrayList<>();

        playerPermissions.removeIf(s -> s.split(":")[0].equals(permission));
        MessageUtils.sendAdminMessage(sender, new TextComponent("Unset permission " + permission + " of " + targetPlayer.getName()));

        Main.get().getDataConfig().set("players." + targetPlayer.getUniqueId() + ".permissions", playerPermissions);
        Main.get().getDataConfig().save();
    }

    private static void playerGroupManager(CommandSender sender, CommandArguments args) {
        OfflinePlayer targetPlayer = (OfflinePlayer) args.get("target");
        String action = (String) args.get("mode");
        String group = (String) args.get("group");

        List<String> playerGroups = Main.get().getDataConfig().getStringList("players." + targetPlayer.getUniqueId() + ".groups");
        if (playerGroups.equals(new ArrayList<>())) playerGroups = new ArrayList<>();

        if (action.equals("set")) {
            if (!playerGroups.contains(group)) playerGroups.add(group);
            MessageUtils.sendAdminMessage(sender, new TextComponent("Added player " + targetPlayer.getPlayer().getName() + " to group " + group));
        } else {
            playerGroups.remove(group);
            MessageUtils.sendAdminMessage(sender, new TextComponent("Removed player " + targetPlayer.getPlayer().getName() + " from group " + group));
        }

        Main.get().getDataConfig().set("players." + targetPlayer.getUniqueId() + ".groups", playerGroups);
        Main.get().getDataConfig().save();
    }

    private static void groupSetPermissionManager(CommandSender sender, CommandArguments args) {
        String groupName = (String) args.get("group");
        String permission = (String) args.get("permission");
        Boolean value = (Boolean) args.get("value");

        List<String> groupPermissions = Main.get().getDataConfig().getStringList("groups." + groupName + ".permissions");

        groupPermissions.removeIf(s -> s.split(":")[0].equals(permission));
        groupPermissions.add(permission + ":" + value);
        MessageUtils.sendAdminMessage(sender, new TextComponent("Set permission " + permission + " of group " + groupName));

        Main.get().getDataConfig().set("groups." + groupName + ".permissions", groupPermissions);
        Main.get().getDataConfig().save();
    }

    private static void groupUnsetPermissionManager(CommandSender sender, CommandArguments args) {
        String groupName = (String) args.get("group");
        String permission = (String) args.get("permission");

        List<String> groupPermissions = Main.get().getDataConfig().getStringList("groups." + groupName + ".permissions");

        groupPermissions.removeIf(s -> s.split(":")[0].equals(permission));
        MessageUtils.sendAdminMessage(sender, new TextComponent("Unset permission " + permission + " of group " + groupName));

        Main.get().getDataConfig().set("groups." + groupName + ".permissions", groupPermissions);
        Main.get().getDataConfig().save();
    }

}