package timongcraft.system.commands;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import timongcraft.system.Main;
import timongcraft.system.util.PlayerOnlyArgument;
import timongcraft.system.util.PlayerUtils;

public class FlySpeedCommand {
    public static void register() {
        new CommandTree("flyspeed")
                .withShortDescription("Set fly speed")
                .withUsage("/flyspeed [<speed>] [<target>]")
                .withAliases("fs")
                .withPermission("tgc-system.team")
                .then(new IntegerArgument("speed", 1, 10)
                        .setOptional(true)
                        .executes(FlySpeedCommand::flySpeedManager)
                        .then(new PlayerOnlyArgument("target")
                                .setOptional(true)
                                .executes(FlySpeedCommand::flySpeedManager)))
                .register();
    }

    private static int flySpeedManager(CommandSender sender, CommandArguments args) {
        if (args.args().length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(Main.get().getPrefix() + "§cUsage: /flyspeed [<flyspeed>] [<player>]");
                return 1;
            }

            player.setFlySpeed(0.1f);
            player.sendMessage(Main.get().getPrefix() + "Fly speed has been reset");
            PlayerUtils.sendToTeam(player.getName(), null, "Reset his fly speed");
            return 1;
        } else if (args.args().length == 1 || (args.args().length == 2 && sender == args.get("target"))) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(Main.get().getPrefix() + "§cUsage: /flyspeed [<flyspeed>] [<player>]");
                return 1;
            }

            float flySpeed = 0.2f;

            if ((int) args.get("speed") <= 9) {
                flySpeed = Float.parseFloat("0." + args.get("speed"));
            } else if ((int) args.get("speed") == 10) flySpeed = 1f;

            player.setFlySpeed(flySpeed);
            if ((int) args.get("speed") == 1) {
                player.sendMessage(Main.get().getPrefix() + "Fly speed has been reset");
                PlayerUtils.sendToTeam(player.getName(), null, "Reset his fly speed");
            } else {
                player.sendMessage(Main.get().getPrefix() + "Set fly speed to " + args.get("speed"));
                PlayerUtils.sendToTeam(player.getName(), null, "Set his fly speed to " + args.get("speed"));
            }
            return 1;
        } else if (args.args().length == 2) {
            float flySpeed = 0.2f;

            if ((int) args.get("speed") <= 9) {
                flySpeed = Float.parseFloat("0." + args.get("speed"));
            } else if ((int) args.get("speed") == 10) flySpeed = 1f;

            Player target = (Player) args.get("target");
            target.setFlySpeed(flySpeed);
            if ((int) args.get("speed") == 1) {
                target.sendMessage(Main.get().getPrefix() + "Fly speed has been reset by " + sender.getName());
                sender.sendMessage(Main.get().getPrefix() + "Reset fly speed of " + target.getName());
                PlayerUtils.sendToTeam(sender.getName(), target.getName(), "Reset the fly speed of " + target.getName());
            } else {
                target.sendMessage(Main.get().getPrefix() + "Fly speed has been set to " + args.get("speed") + " by " + sender.getName());
                sender.sendMessage(Main.get().getPrefix() + "Set fly speed of " + target.getName() + " to " + args.get("speed"));
                PlayerUtils.sendToTeam(sender.getName(), target.getName(), "Set the fly speed of " + target.getName() + " to " + args.get("speed"));
            }
            return 1;
        }
        return 0;
    }

}
