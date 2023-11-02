package timongcraft.system.commands;

import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.executors.CommandArguments;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import timongcraft.system.Main;
import timongcraft.system.util.MessageUtils;

import java.util.List;

public class CoordinatesCommand {

    public static void register() {
        new CommandTree("coordinates")
                .withShortDescription("A more vanilla like alternative to waypoints")
                .withUsage(
                        "/coords",
                        "/coords <add|remove> <name>"
                )
                .withAliases("coords")
                .executesPlayer(CoordinatesCommand::coordsList)
                .then(new LiteralArgument("list")
                        .withPermission(CommandPermission.OP)
                        .then(new OfflinePlayerArgument("target")
                                .replaceSuggestions(ArgumentSuggestions.strings(info ->
                                        Bukkit.getOnlinePlayers().stream().map(OfflinePlayer::getName).toArray(String[]::new)))
                                .executes(CoordinatesCommand::coordsListOther)))
                .then(new LiteralArgument("add")
                        .then(new TextArgument("name")
                                .executesPlayer(CoordinatesCommand::coordsAdder)))
                .then(new LiteralArgument("remove")
                        .then(new GreedyStringArgument("name")
                                .includeSuggestions(ArgumentSuggestions.strings(info -> getCoordinates((Player) info.sender())))
                                .executesPlayer(CoordinatesCommand::coordsRemover)))
                .then(new LiteralArgument("get")
                        .then(new GreedyStringArgument("name")
                                .includeSuggestions(ArgumentSuggestions.strings(info -> getCoordinates((Player) info.sender())))
                                .executesPlayer(CoordinatesCommand::coordsGrabber)))
                .then(new LiteralArgument("save")
                        .then(new GreedyStringArgument("coords")
                                .executesPlayer(CoordinatesCommand::coordsSaver)))
                .register();
    }

    private static String[] getCoordinates(Player sender) {
        List<String> coordinatesList = Main.get().getDataConfig().getStringList("players." + sender.getUniqueId() + ".coords");
        if (coordinatesList.isEmpty()) return new String[0];

        String[] coordinateNames = new String[coordinatesList.size()];
        int index = 0;

        for (String coordinateNamesName : coordinatesList)
            coordinateNames[index++] = (coordinateNamesName.split(":")[0]);

        return coordinateNames;
    }

    private static int coordsList(Player sender, CommandArguments args) {
        List<String> coordinatesList = Main.get().getDataConfig().getStringList("players." + sender.getUniqueId() + ".coords");

        if (coordinatesList.isEmpty()) {
            sender.sendMessage(Main.get().getPrefix() + "§cYou haven't any saved coordinates");
            return 0;
        }

        sender.sendMessage("Saved Coordinates:\n");
        for (String coordinate : coordinatesList) {
            String coordinateName = coordinate.split(":")[0];
            String coordinateCoordinates = coordinate.split(":")[1].replaceAll(",", " ");
            ChatColor color = getEnvironmentColor(coordinate.split(":")[2]);

            TextComponent coordinateNameComponent = new TextComponent(color + coordinateName);
            coordinateNameComponent.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "[coords:" + coordinateName + "]"));
            coordinateNameComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§6Click to copy.")));

            TextComponent coordinateCoordinatesComponent = new TextComponent(color + " (" + coordinateCoordinates + ")");
            coordinateCoordinatesComponent.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, coordinateCoordinates));
            coordinateCoordinatesComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§6Click to copy raw coordinates.")));

            sender.spigot().sendMessage(new TextComponent(" - "), coordinateNameComponent, coordinateCoordinatesComponent);
        }

        return 1;
    }

    private static int coordsListOther(CommandSender sender, CommandArguments args) {
        OfflinePlayer target = (OfflinePlayer) args.get("target");
        List<String> coordinatesList = Main.get().getDataConfig().getStringList("players." + target.getUniqueId() + ".coords");

        if (coordinatesList.isEmpty()) {
            sender.sendMessage(Main.get().getPrefix() + "§c" + target.getName() + " hasn't saved any coordinates");
            return 0;
        }

        sender.sendMessage(Main.get().getPrefix() + target.getName() + "s Saved Coordinates:\n");
        for (String coordinate : coordinatesList) {
            String coordinateName = coordinate.split(":")[0];
            String coordinateCoordinates = coordinate.split(":")[1].replaceAll(",", " ");
            ChatColor color = getEnvironmentColor(coordinate.split(":")[2]);

            TextComponent coordinateNameComponent = new TextComponent(color + coordinateName);
            coordinateNameComponent.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "[coords:" + coordinateName + "]"));
            coordinateNameComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§6Click to copy.")));

            TextComponent coordinateCoordinatesComponent = new TextComponent(color + " (" + coordinateCoordinates + ")");
            coordinateCoordinatesComponent.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, coordinateCoordinates));
            coordinateCoordinatesComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§6Click to copy coordinates.")));

            sender.spigot().sendMessage(new TextComponent(" - "), coordinateNameComponent, coordinateCoordinatesComponent);
        }

        return 1;
    }

    private static int coordsAdder(Player sender, CommandArguments args) {
        String coordinateName = (String) args.get("name");
        Location location = sender.getLocation().getBlock().getLocation();
        String coordinate = String.format("%d,%d,%d", (int) location.getX(), (int) location.getY(), (int) location.getZ());
        String coordinateClean = coordinate.replaceAll(",", " ");
        List<String> coordinatesList = Main.get().getDataConfig().getStringList("players." + sender.getUniqueId() + ".coords");
        int coordinatesLimit = Main.get().getConfig().getInt("coordsSaver.limit");

        if (coordinatesLimit != -1 && coordinatesList.size() >= coordinatesLimit) {
            sender.sendMessage(Main.get().getPrefix() + "§cYou succeeded the limit of " + coordinatesLimit + " coordinates");
            return 0;
        }

        coordinatesList.removeIf(searchedCoordinate -> searchedCoordinate.startsWith(coordinateName + ":"));
        coordinatesList.add(coordinateName + ":" + coordinate + ":" + sender.getWorld().getEnvironment().name());
        Main.get().getDataConfig().set("players." + sender.getUniqueId() + ".coords", coordinatesList);
        Main.get().getDataConfig().save();

        ChatColor color = getEnvironmentColor(sender.getWorld().getEnvironment().name());

        TextComponent coordinateNameComponent = new TextComponent(color + coordinateName);
        coordinateNameComponent.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "[coords:" + coordinateName + "]"));
        coordinateNameComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§6Click to copy.")));

        TextComponent coordinateCoordinatesComponent = new TextComponent(color + " (" + coordinateClean + ")");
        coordinateCoordinatesComponent.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, coordinateClean));
        coordinateCoordinatesComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§6Click to copy coordinates.")));

        MessageUtils.sendAdminMessage(sender, new TextComponent("Added coordinate: " + coordinateName + " (" + coordinateClean + ")"));
        return 1;
    }

    private static int coordsRemover(Player sender, CommandArguments args) {
        String coordinateName = (String) args.get("name");
        List<String> coordinatesList = Main.get().getDataConfig().getStringList("players." + sender.getUniqueId() + ".coords");

        if (coordinatesList.isEmpty()) {
            sender.sendMessage(Main.get().getPrefix() + "§cThere aren't any saved coordinates");
            return 0;
        }

        for (String coordinate : coordinatesList) {
            if (coordinate.startsWith(coordinateName + ":")) {
                coordinatesList.remove(coordinate);
                Main.get().getDataConfig().set("players." + sender.getUniqueId() + ".coords", coordinatesList);
                Main.get().getDataConfig().save();

                MessageUtils.sendAdminMessage(sender, new TextComponent("Removed coordinate: " + coordinateName));
                return 1;
            }
        }

        sender.sendMessage(Main.get().getPrefix() + "§cNo coordinate with the name: " + coordinateName + "could be found");
        return 0;
    }

    private static int coordsGrabber(Player sender, CommandArguments args) {
        String coordinateName = (String) args.get("name");
        List<String> coordinatesList = Main.get().getDataConfig().getStringList("players." + sender.getUniqueId() + ".coords");

        if (coordinatesList.isEmpty()) {
            sender.sendMessage(Main.get().getPrefix() + "§cThere aren't any saved coordinates");
            return 0;
        }

        for (String coordinate : coordinatesList) {
            if (coordinate.startsWith(coordinateName + ":")) {
                String coordinateCoordinates = coordinate.split(":")[1].replaceAll(",", " ");
                ChatColor color = getEnvironmentColor(coordinate.split(":")[2]);

                TextComponent coordinateNameComponent = new TextComponent(color + coordinateName);
                coordinateNameComponent.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "[coords:" + coordinateName + "]"));
                coordinateNameComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§6Click to copy.")));

                TextComponent coordinateCoordinatesComponent = new TextComponent(color + " (" + coordinateCoordinates + ")");
                coordinateCoordinatesComponent.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, coordinateCoordinates));
                coordinateCoordinatesComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§6Click to copy coordinates.")));

                sender.spigot().sendMessage(new TextComponent(Main.get().getPrefix() + "Coordinates of "), coordinateNameComponent, new TextComponent(": "), coordinateCoordinatesComponent);
                return 1;
            }
        }

        sender.sendMessage(Main.get().getPrefix() + "§cNo coordinate with the name: " + coordinateName + "could be found");
        return 0;
    }

    private static void coordsSaver(Player sender, CommandArguments args) {
        String coords = (String) args.get("coords");
        String coordsName = coords.split(":")[0];
        ChatColor color = getEnvironmentColor(coords.split(":")[2]);
        List<String> coordinatesList = Main.get().getDataConfig().getStringList("players." + sender.getUniqueId() + ".coords");

        coordinatesList.removeIf(searchedCoordinate -> searchedCoordinate.startsWith(coordsName));
        coordinatesList.add(coords);
        Main.get().getDataConfig().set("players." + sender.getUniqueId() + ".coords", coordinatesList);
        Main.get().getDataConfig().save();

        sender.sendMessage(Main.get().getPrefix() + "Added " + color + coordsName + "§r to your coords list");
    }

    public static ChatColor getEnvironmentColor(String environment) {
        return switch (environment) {
            case "NORMAL" -> ChatColor.GREEN;
            case "NETHER" -> ChatColor.RED;
            case "THE_END" -> ChatColor.LIGHT_PURPLE;
            case "CUSTOM" -> ChatColor.BLUE;
            default -> null;
        };

    }

}