package timongcraft.system.commands;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import timongcraft.system.Main;
import timongcraft.system.util.PlayerOnlyArgument;
import timongcraft.system.util.PlayerUtils;

public class SpeedCommand {
    public static void register() {
        new CommandTree("speed")
                .withShortDescription("Set fly & walk speed")
                .withUsage("/speed [<speed>] [<target>]")
                .withPermission("tgc-system.team")
                .then(new IntegerArgument("speed", 1, 10)
                        .setOptional(true)
                        .executes(SpeedCommand::speedManager)
                        .then(new PlayerOnlyArgument("target")
                                .setOptional(true)
                                .executes(SpeedCommand::speedManager)))
                .register();
    }

    private static int speedManager(CommandSender sender, CommandArguments args) {
        if(args.args().length == 0) {
            if(!(sender instanceof Player player)) {
                sender.sendMessage(Main.get().getPrefix() + "§cUsage: /speed [<speed>] [<player>]");
                return 1;
            }

            player.setWalkSpeed(0.2f);
            player.setFlySpeed(0.1f);
            player.sendMessage(Main.get().getPrefix() + "Speed has been reset");
            PlayerUtils.sendToTeam(player.getName(), null, "Reset his speed");
        } else if(args.args().length == 1 || (args.args().length == 2 && sender == args.get("target"))) {
            if(!(sender instanceof Player player)) {
                sender.sendMessage(Main.get().getPrefix() + "§cUsage: /speed [<speed>] [<player>]");
                return 1;
            }

            float speed = 0.1f;

            if((int) args.get("speed") <= 9) {
                speed = Float.parseFloat("0." + args.get("speed"));
            } else if((int) args.get("speed") == 10) speed = 1f;

            player.setWalkSpeed(speed);
            player.setFlySpeed(speed);
            if((int) args.get("speed") == 1) {
                player.sendMessage(Main.get().getPrefix() + "Speed has been reset");
                PlayerUtils.sendToTeam(player.getName(), null, "Reset his speed");
            } else {
                player.sendMessage(Main.get().getPrefix() + "Set speed to " + args.get("speed"));
                PlayerUtils.sendToTeam(player.getName(), null, "Set his speed to " + args.get("speed"));
            }
            return 1;
        } else if(args.args().length == 2) {
            float speed = 0.1f;

            if((int) args.get("speed") <= 9) {
                speed = Float.parseFloat("0." + args.get("speed"));
            } else if((int) args.get("speed") == 10) speed = 1f;

            Player target = (Player) args.get("target");
            target.setWalkSpeed(speed);
            target.setFlySpeed(speed);
            if((int) args.get("speed") == 1) {
                target.sendMessage(Main.get().getPrefix() + "Speed has been reset by " + sender.getName());
                sender.sendMessage(Main.get().getPrefix() + "Reset speed of " + target.getName());
                PlayerUtils.sendToTeam(sender.getName(), target.getName(), "Reset the speed of " + target.getName());
            } else {
                target.sendMessage(Main.get().getPrefix() + "Speed has been set to " + args.get("speed") + " by " + sender.getName());
                sender.sendMessage(Main.get().getPrefix() + "Set speed of " + target.getName() + " to " + args.get("speed"));
                PlayerUtils.sendToTeam(sender.getName(), target.getName(), "Set the speed of " + target.getName() + " to " + args.get("speed"));
            }
            return 1;
        }
        return 0;
    }
}
