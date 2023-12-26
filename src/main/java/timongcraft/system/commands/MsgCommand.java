package timongcraft.system.commands;

import dev.jorel.commandapi.CommandAPIBukkit;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import timongcraft.system.Main;
import timongcraft.system.util.CoordsMessageUtils;
import timongcraft.system.util.PlayerOnlyArgument;

public class MsgCommand {

    public static void register() {
        CommandAPIBukkit.unregister("msg", true, false);
        CommandAPIBukkit.unregister("tell", true, false);
        CommandAPIBukkit.unregister("w", true, false);

        new CommandTree("msg")
                .withShortDescription("Send a private message to a player")
                .withUsage("/msg <target> <message>")
                .withAliases("tell", "w")
                .then(new PlayerOnlyArgument("target")
                        .then(new GreedyStringArgument("message")
                                .executes(MsgCommand::msgManager)))
                .register();
    }

    private static void msgManager(CommandSender sender, CommandArguments args) {
        Player target = (Player) args.get("target");
        String rawMessage = (String) args.get("message");

        if (sender.hasPermission("tgc-system.team"))
            rawMessage = rawMessage.replaceAll("&", "§");

        TextComponent message = new TextComponent(rawMessage);

        if (sender instanceof Player player && Main.get().getConfig().getBoolean("coordsSaver.enabled"))
            if (Main.get().getConfig().getBoolean("coordsSaver.xaerosWaypointCompatability")) {
                TextComponent xaerosWaypoint = CoordsMessageUtils.getXaerosWaypointAsClickableCoordinatesMessage(rawMessage);

                if (xaerosWaypoint != null) {
                    message = xaerosWaypoint;
                } else {
                    message = CoordsMessageUtils.getAsClickableCoordinatesMessage(player, rawMessage, true);
                }
            } else {
                message = CoordsMessageUtils.getAsClickableCoordinatesMessage(player, rawMessage, false);
            }

        sender.spigot().sendMessage(new ComponentBuilder(new TranslatableComponent("commands.message.display.outgoing", target.getName(), message)).color(ChatColor.GRAY).italic(true).create());
        target.spigot().sendMessage(new ComponentBuilder(new TranslatableComponent("commands.message.display.incoming", sender.getName(), message)).color(ChatColor.GRAY).italic(true).create());
        if (sender instanceof Player player)
            ReplyCommand.setLastReply(player.getUniqueId(), target.getUniqueId());
    }

}