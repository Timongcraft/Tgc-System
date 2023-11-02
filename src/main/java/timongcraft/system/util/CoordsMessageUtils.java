package timongcraft.system.util;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import timongcraft.system.Main;
import timongcraft.system.commands.CoordinatesCommand;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoordsMessageUtils {

    public static TextComponent getAsClickableCoordinatesMessage(Player player, String message, boolean withXaerosWaypoint) {
        Pattern coordsPattern = Pattern.compile("\\[coords(:.+)?]");
        Matcher coordsTextMatcher = coordsPattern.matcher(message);
        TextComponent finalMessage = new TextComponent();

        int lastEndIndex = 0;
        while (coordsTextMatcher.find()) {
            String match = coordsTextMatcher.group();
            String coordName = match.equals("[coords]") ? null : match.substring(8, match.length() - 1);

            finalMessage.addExtra(new TextComponent(message.substring(lastEndIndex, coordsTextMatcher.start())));

            TextComponent coordsComponent = new TextComponent();
            if (coordName == null) {
                Location playerLocation = player.getLocation().getBlock().getLocation();
                String playerCoords = (int) playerLocation.getX() + " " + (int) playerLocation.getY() + " " + (int) playerLocation.getZ();
                ChatColor color = CoordinatesCommand.getEnvironmentColor(player.getWorld().getEnvironment().name());

                coordsComponent = new TextComponent(color + "[" + playerCoords + "]§r");
                coordsComponent.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, playerCoords));
                coordsComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§6Click to copy raw coordinates.")));

                if (withXaerosWaypoint) {
                    String coords = player.getName() + ":" + playerCoords.replaceAll(" ", ",") + ":" + player.getWorld().getEnvironment().name();
                    TextComponent xaerosComponent = new TextComponent(" §8§k" + getXaerosWaypoint(coords.split(":")));
                    xaerosComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://modrinth.com/mod/xaeros-minimap"));
                    xaerosComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§6This is for players using the xaero's minimap mod.")));
                    coordsComponent.addExtra(xaerosComponent);
                }
            } else {
                List<String> coordinatesList = Main.get().getDataConfig().getStringList("players." + player.getUniqueId() + ".coords");
                boolean found = false;
                for (String coordinate : coordinatesList) {
                    String coordinateName = coordinate.split(":")[0];
                    if (coordName.equals(coordinateName)) {
                        ChatColor color = CoordinatesCommand.getEnvironmentColor(coordinate.split(":")[2]);
                        String coordinateCoordinates = coordinate.split(":")[1].replaceAll(",", " ");

                        TextComponent nameComponent = new TextComponent(color + "[" + coordinateName);
                        nameComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/coords save " + coordinate));
                        nameComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§6Click to save.")));
                        coordsComponent.addExtra(nameComponent);

                        TextComponent coordsCordsComponent = new TextComponent(color + " (" + coordinateCoordinates + ")" + "]§r");
                        coordsCordsComponent.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, coordinateCoordinates));
                        coordsCordsComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§6Click to copy raw coordinates.")));
                        coordsComponent.addExtra(coordsCordsComponent);

                        if (withXaerosWaypoint) {
                            TextComponent xaerosComponent = new TextComponent(" §8§k" + getXaerosWaypoint(coordinate.split(":")));
                            xaerosComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://modrinth.com/mod/xaeros-minimap"));
                            xaerosComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§6This is for players using the xaero's minimap mod.")));
                            coordsComponent.addExtra(xaerosComponent);
                        }

                        found = true;
                        break;
                    }
                }

                if (!found) {
                    coordsComponent = new TextComponent("§4[" + coordName + " not found]");
                    coordsComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§4The player sent invalid coordinates.")));
                }
            }

            finalMessage.addExtra(coordsComponent);

            lastEndIndex = coordsTextMatcher.end();
        }

        finalMessage.addExtra(new TextComponent(message.substring(lastEndIndex)));
        return finalMessage;
    }

    public static TextComponent getXaerosWaypointAsClickableCoordinatesMessage(String message) {
        Pattern pattern = Pattern.compile("xaero-waypoint:[\\w\\s]+:\\w:[-]?\\d+:[-]?\\d+:[-]?\\d+:[-]?\\d+:(true|false):[-]?\\d+:Internal-\\w+-waypoints");
        Matcher matcher = pattern.matcher(message);

        if (!matcher.matches()) return null;

        String[] xaerosWaypoint = message.split(":");
        TextComponent coordsComponent = new TextComponent();

        String coordinateName = xaerosWaypoint[1];
        String cleanCoordinateCoordinates = xaerosWaypoint[3] + " " + xaerosWaypoint[4] + " " + xaerosWaypoint[5];
        String coordinateEnvironment = getEnvironment(xaerosWaypoint[9]);
        ChatColor color = CoordinatesCommand.getEnvironmentColor(coordinateEnvironment);
        String coordinate = coordinateName + ":" + cleanCoordinateCoordinates.replaceAll(" ", ",") + ":" + coordinateEnvironment;

        TextComponent nameComponent = new TextComponent(color + "[" + coordinateName);
        nameComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/coords save " + coordinate));
        nameComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§6Click to save.")));
        coordsComponent.addExtra(nameComponent);

        TextComponent coordsCordsComponent = new TextComponent(color + " (" + cleanCoordinateCoordinates + ")" + "]§r");
        coordsCordsComponent.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, cleanCoordinateCoordinates));
        coordsCordsComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§6Click to copy raw coords.")));
        coordsComponent.addExtra(coordsCordsComponent);

        TextComponent xaerosComponent = new TextComponent(" §8§k" + getXaerosWaypoint(coordinate.split(":")));
        xaerosComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://modrinth.com/mod/xaeros-minimap"));
        xaerosComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§6This is for players using the xaero's minimap mod.")));
        coordsComponent.addExtra(xaerosComponent);

        return coordsComponent;
    }

    private static String getXaerosWaypoint(String[] coords) {
        return "xaero-waypoint:" + coords[0] + ":" + coords[0].charAt(0) + ":" + coords[1].replaceAll(",", ":") + ":0:false:0:" + switch (coords[2]) {
            case "NORMAL" -> "Internal-overworld-waypoints";
            case "NETHER" -> "Internal-the-nether-waypoints";
            case "THE_END" -> "Internal-the-end-waypoints";
            default -> "Internal-CUSTOM-waypoints";
        };
    }

    private static String getEnvironment(String xaerosWorld) {
        return switch (xaerosWorld) {
            case "Internal-overworld-waypoints" -> "NORMAL";
            case "Internal-the-nether-waypoints" -> "NETHER";
            case "Internal-the-end-waypoints" -> "THE_END";
            default -> "CUSTOM";
        };
    }

}