package timongcraft.system.commands;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import timongcraft.system.Main;
import timongcraft.system.util.MessageUtils;
import timongcraft.system.util.PlayerOnlyArgument;

public class FlySpeedCommand {

    public static void register() {
        new CommandTree("flyspeed")
                .withShortDescription("Set fly speed")
                .withUsage("/flyspeed [<speed>] [<target>]")
                .withAliases("fs")
                .withPermission("tgc-system.team")
                .executesPlayer(FlySpeedCommand::flySpeedOwnResetManager)
                .then(new IntegerArgument("speed", 2, 10)
                        .executesPlayer(FlySpeedCommand::flySpeedOwnManager)
                        .then(new PlayerOnlyArgument("target")
                                .executes(FlySpeedCommand::flySpeedOtherManager)))
                .register();
    }

    private static void flySpeedOwnResetManager(Player sender, CommandArguments args) {
        sender.setFlySpeed(0.2f);
        MessageUtils.sendAdminMessage(sender, new TextComponent("Fly speed has been reset"));
    }

    private static void flySpeedOwnManager(Player sender, CommandArguments args) {
        sender.setFlySpeed((int) args.get("speed") / 10f);
        MessageUtils.sendAdminMessage(sender, new TextComponent("Set fly speed to " + args.get("speed")));
    }


    private static void flySpeedOtherManager(CommandSender sender, CommandArguments args) {
        Player target = (Player) args.get("target");
        target.setFlySpeed((int) args.get("speed") / 10f);
        target.sendMessage(Main.get().getPrefix() + "Fly speed has been set to " + args.get("speed") + " by " + sender.getName());
        MessageUtils.sendAdminMessage(sender, new TextComponent("Set fly speed of " + target.getName() + " to " + args.get("speed")));
    }

}
