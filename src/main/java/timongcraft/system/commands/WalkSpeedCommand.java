package timongcraft.system.commands;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import timongcraft.system.Main;
import timongcraft.system.util.PlayerOnlyArgument;
import timongcraft.system.util.PlayerUtils;

public class WalkSpeedCommand {
    public static void register() {
        new CommandTree("walkspeed")
                .withShortDescription("Set walk speed")
                .withUsage("/walkspeed [<speed>] [<target>]")
                .withAliases("ws")
                .withPermission("tgc-system.team")
                .then(new IntegerArgument("walkspeed", 1, 10)
                        .setOptional(true)
                        .executes(WalkSpeedCommand::walkSpeedManager)
                        .then(new PlayerOnlyArgument("target")
                                .setOptional(true)
                                .executes(WalkSpeedCommand::walkSpeedManager)))
                .register();
    }

    private static int walkSpeedManager(CommandSender sender, CommandArguments args) {
        if (args.args().length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(Main.get().getPrefix() + "§cUsage: /walkspeed [<walkspeed>] [<player>]");
                return 1;
            }

            player.setWalkSpeed(0.2f);
            player.sendMessage(Main.get().getPrefix() + "Walk speed has been reset");
            PlayerUtils.sendToTeam(player.getName(), null, "Reset his walk speed");
        } else if (args.args().length == 1 || (args.args().length == 2 && sender == args.get("target"))) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(Main.get().getPrefix() + "§cUsage: /walkspeed [<walkspeed>] [<player>]");
                return 1;
            }

            float walkSpeed = 0.2f;

            if ((int) args.get("speed") <= 9) {
                walkSpeed = Float.parseFloat("0." + args.get("speed"));
            } else if ((int) args.get("speed") == 10) walkSpeed = 1f;

            player.setWalkSpeed(walkSpeed);
            if ((int) args.get("walkspeed") == 1) {
                player.sendMessage(Main.get().getPrefix() + "Walk speed has been reset");
                PlayerUtils.sendToTeam(player.getName(), null, "Reset his walk speed");
            } else {
                player.sendMessage(Main.get().getPrefix() + "Set walk speed to " + args.get("walkspeed"));
                PlayerUtils.sendToTeam(player.getName(), null, "Set his walk speed to " + args.get("walkspeed"));
            }
            return 1;
        } else if (args.args().length == 2) {
            float walkSpeed = 0.2f;

            if ((int) args.get("speed") <= 9) {
                walkSpeed = Float.parseFloat("0." + args.get("speed"));
            } else if ((int) args.get("speed") == 10) walkSpeed = 1f;

            Player target = (Player) args.get("target");
            target.setWalkSpeed(walkSpeed);
            if ((int) args.get("walkspeed") == 1) {
                target.sendMessage(Main.get().getPrefix() + "Walk speed has been reset by " + sender.getName());
                sender.sendMessage(Main.get().getPrefix() + "Reset walk speed of " + target.getName());
                PlayerUtils.sendToTeam(sender.getName(), target.getName(), "Reset the walk speed of " + target.getName());
            } else {
                target.sendMessage(Main.get().getPrefix() + "Walk speed has been set to " + args.get("walkspeed") + " by " + sender.getName());
                sender.sendMessage(Main.get().getPrefix() + "Set walk speed of " + target.getName() + " to " + args.get("walkspeed"));
                PlayerUtils.sendToTeam(sender.getName(), target.getName(), "Set the walk speed of " + target.getName() + " to " + args.get("walkspeed"));
            }
            return 1;
        }
        return 0;
    }

}
