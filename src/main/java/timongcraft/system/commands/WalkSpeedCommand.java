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

public class WalkSpeedCommand {

    public static void register() {
        new CommandTree("walkspeed")
                .withShortDescription("Set walk speed")
                .withUsage("/walkspeed [<speed>] [<target>]")
                .withAliases("ws")
                .withPermission("tgc-system.team")
                .executesPlayer(WalkSpeedCommand::walkSpeedOwnResetManager)
                .then(new IntegerArgument("speed", 1, 10)
                        .executesPlayer(WalkSpeedCommand::walkSpeedOwnManager)
                        .then(new PlayerOnlyArgument("target")
                                .executes(WalkSpeedCommand::walkSpeedOtherManager)))
                .register();
    }

    private static void walkSpeedOwnResetManager(Player sender, CommandArguments args) {
        sender.setWalkSpeed(0.1f);
        MessageUtils.sendAdminMessage(sender, new TextComponent("Walk speed has been reset"));
    }

    private static void walkSpeedOwnManager(Player sender, CommandArguments args) {
        sender.setWalkSpeed((int) args.get("speed") / 10f);
        MessageUtils.sendAdminMessage(sender, new TextComponent("Set walk speed to " + args.get("speed")));
    }


    private static void walkSpeedOtherManager(CommandSender sender, CommandArguments args) {
        Player target = (Player) args.get("target");
        target.setWalkSpeed((int) args.get("speed") / 10f);
        target.sendMessage(Main.get().getPrefix() + "Walk speed has been set to " + args.get("speed") + " by " + sender.getName());
        MessageUtils.sendAdminMessage(sender, new TextComponent("Set walk speed of " + target.getName() + " to " + args.get("speed")));
    }

}
