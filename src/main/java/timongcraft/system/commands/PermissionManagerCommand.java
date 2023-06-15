package timongcraft.system.commands;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import timongcraft.system.Main;
import timongcraft.system.util.PlayerUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
                                        .then(new MultiLiteralArgument("mode", List.of("set", "unset"))
                                                .then(new StringArgument("group")
                                                        .includeSuggestions(ArgumentSuggestions.strings(getGroups()))
                                                        .executes(PermissionManagerCommand::playerGroupManager))))))
                .then(new LiteralArgument("group")
                        .then(new StringArgument("group")
                                .includeSuggestions(ArgumentSuggestions.strings(getGroups()))
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
        if (groupsSection == null) {
            return new String[0];
        }

        Set<String> topLevelGroups = groupsSection.getKeys(false);
        return topLevelGroups.toArray(new String[0]);
    }

    private static String[] getPermissionsForPlayer(OfflinePlayer playerName) {
        List<String> permissionsList = Main.get().getDataConfig().getStringList("players." + playerName.getUniqueId() + ".permissions");
        List<String> permissionNames = new ArrayList<>();

        for (String permissionString : permissionsList) {
            String permissionName = permissionString.split(":")[0];
            permissionNames.add(permissionName);
        }

        return permissionNames.toArray(new String[0]);
    }

    private static String[] getPermissionsForGroup(String groupName) {
        List<String> permissionsList = Main.get().getDataConfig().getStringList("groups." + groupName + ".permissions");
        List<String> permissionNames = new ArrayList<>();

        for (String permissionString : permissionsList) {
            String permissionName = permissionString.split(":")[0];
            permissionNames.add(permissionName);
        }

        return permissionNames.toArray(new String[0]);
    }

    private static int permissionSetManager(CommandSender sender, CommandArguments args) {
        OfflinePlayer targetPlayer = (OfflinePlayer) args.get("target");
        String permission = (String) args.get("permission");
        Boolean value = (Boolean) args.get("value");

        List<String> playerPermissions = Main.get().getDataConfig().getStringList("players." + targetPlayer.getUniqueId() + ".permissions");
        if (playerPermissions.equals(new ArrayList<>())) playerPermissions = new ArrayList<>();

        String permissionString = permission + ":" + value;

        playerPermissions.removeIf(s -> s.split(":")[0].equals(permission));
        playerPermissions.add(permissionString);
        sender.sendMessage(Main.get().getPrefix() + "Set permission " + permission + " for player " + targetPlayer.getName());
        PlayerUtils.sendToTeam(sender.getName(), null, "Set permission " + permission + " from " + targetPlayer.getName());

        Main.get().getDataConfig().set("players." + targetPlayer.getUniqueId() + ".permissions", playerPermissions);
        Main.get().getDataConfig().save();
        return 1;
    }

    private static int permissionUnsetManager(CommandSender sender, CommandArguments args) {
        OfflinePlayer targetPlayer = (OfflinePlayer) args.get("target");
        String permission = (String) args.get("permission");

        List<String> playerPermissions = Main.get().getDataConfig().getStringList("players." + targetPlayer.getUniqueId() + ".permissions");
        if (playerPermissions.equals(new ArrayList<>())) playerPermissions = new ArrayList<>();

        playerPermissions.removeIf(s -> s.split(":")[0].equals(permission));
        sender.sendMessage(Main.get().getPrefix() + "Unset permission " + permission + " from player " + targetPlayer.getName());
        PlayerUtils.sendToTeam(sender.getName(), null, "Unset permission " + permission + " from " + targetPlayer.getName());

        Main.get().getDataConfig().set("players." + targetPlayer.getUniqueId() + ".permissions", playerPermissions);
        Main.get().getDataConfig().save();
        return 1;
    }

    private static int playerGroupManager(CommandSender sender, CommandArguments args) {
        OfflinePlayer targetPlayer = (OfflinePlayer) args.get("target");
        String action = (String) args.get("mode");
        String group = (String) args.get("group");

        if (targetPlayer == null) {
            sender.sendMessage(Main.get().getPrefix() + "§cPlayer not found");
            return 1;
        }

        List<String> playerGroups = Main.get().getDataConfig().getStringList("players." + targetPlayer.getUniqueId() + ".groups");
        if (playerGroups.equals(new ArrayList<>())) playerGroups = new ArrayList<>();

        if (action.equalsIgnoreCase("set")) {
            if (!playerGroups.contains(group)) playerGroups.add(group);
            sender.sendMessage(Main.get().getPrefix() + "Added player " + targetPlayer.getPlayer().getName() + " to group " + group);
            PlayerUtils.sendToTeam(sender.getName(), null, "Added player " + targetPlayer.getPlayer().getName() + " to group " + group);
        } else if (action.equalsIgnoreCase("unset")) {
            playerGroups.remove(group);
            sender.sendMessage(Main.get().getPrefix() + "Removed player " + targetPlayer.getPlayer().getName() + " from group " + group);
            PlayerUtils.sendToTeam(sender.getName(), null, "Removed player " + targetPlayer.getPlayer().getName() + " from group " + group);
        }

        Main.get().getDataConfig().set("players." + targetPlayer.getUniqueId() + ".groups", playerGroups);
        Main.get().getDataConfig().save();
        return 1;
    }

    private static int groupSetPermissionManager(CommandSender sender, CommandArguments args) {
        String groupName = (String) args.get("group");
        String permission = (String) args.get("permission");
        Boolean value = (Boolean) args.get("value");

        List<String> groupPermissions = Main.get().getDataConfig().getStringList("groups." + groupName + ".permissions");
        if (groupPermissions.equals(new ArrayList<>())) groupPermissions = new ArrayList<>();

        String permissionString = permission + ":" + value;

        groupPermissions.removeIf(s -> s.split(":")[0].equals(permission));
        groupPermissions.add(permissionString);
        sender.sendMessage(Main.get().getPrefix() + "Set permission " + permissionString + " for group " + groupName);
        PlayerUtils.sendToTeam(sender.getName(), null, "Set permission " + permission + " for group " + groupName);

        Main.get().getDataConfig().set("groups." + groupName + ".permissions", groupPermissions);
        Main.get().getDataConfig().save();
        return 1;
    }

    private static int groupUnsetPermissionManager(CommandSender sender, CommandArguments args) {
        String groupName = (String) args.get("group");
        String permission = (String) args.get("permission");

        List<String> groupPermissions = Main.get().getDataConfig().getStringList("groups." + groupName + ".permissions");
        if (groupPermissions.equals(new ArrayList<>())) groupPermissions = new ArrayList<>();

        groupPermissions.removeIf(s -> s.split(":")[0].equals(permission));
        sender.sendMessage(Main.get().getPrefix() + "Unset permission " + permission + " from group " + groupName);
        PlayerUtils.sendToTeam(sender.getName(), null, "Unset permission " + permission + " from group " + groupName);

        Main.get().getDataConfig().set("groups." + groupName + ".permissions", groupPermissions);
        Main.get().getDataConfig().save();
        return 1;
    }
}