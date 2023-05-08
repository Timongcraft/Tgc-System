package timongcraft.commands;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.executors.CommandExecutor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import timongcraft.Main;
import timongcraft.util.TeamUtils;

public class FlySpeedCommand {
    public static void register() {
        new CommandTree("flyspeed")
                .withFullDescription("Set fly speed")
                .withAliases("fs")
                .withPermission("tgc-system.team")
                .then(new IntegerArgument("flyspeed", 1, 10)
                        .setOptional(true)
                        .executes(new FlySpeedExecutor())
                        .then(new EntitySelectorArgument.OnePlayer("target")
                                .setOptional(true)
                                .replaceSuggestions(ArgumentSuggestions.strings(info -> Bukkit.getOnlinePlayers().stream()
                                .map(Player::getName)
                                .toArray(String[]::new)))
                                .executes(new FlySpeedExecutor())))
                .register();
    }

    private static class FlySpeedExecutor implements CommandExecutor {
        @Override
        public void run(CommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException {
            if(args.args().length == 0) {
                if(!(sender instanceof Player player)) {
                    sender.sendMessage(Main.get().getPrefix() + "§cUsage: /flyspeed [<flyspeed>] [<player>]");
                    return;
                }

                player.setFlySpeed(0.1f);
                player.sendMessage(Main.get().getPrefix() + "Fly speed has been reset");
                TeamUtils.sendToTeam(player.getName(), null, "Reset his fly speed");
            } else if(args.args().length == 1 || (args.args().length == 2 && sender == args.get("target"))) {
                if(!(sender instanceof Player player)) {
                    sender.sendMessage(Main.get().getPrefix() + "§cUsage: /flyspeed [<flyspeed>] [<player>]");
                    return;
                }

                float flySpeed = Float.parseFloat(args.get("flyspeed").toString())/10;

                player.setFlySpeed(flySpeed);
                if((int) args.get("flyspeed") == 1) {
                    player.sendMessage(Main.get().getPrefix() + "Fly speed has been reset");
                    TeamUtils.sendToTeam(player.getName(), null, "Reset his fly speed" );
                } else  {
                    player.sendMessage(Main.get().getPrefix() + "Set fly speed to " + args.get("flyspeed"));
                    TeamUtils.sendToTeam(player.getName(), null, "Set his fly speed to " + args.get("flyspeed"));
                }
            } else if(args.args().length == 2) {
                float flySpeed = Float.parseFloat(args.get("flyspeed").toString())/10;
                Player target = (Player) args.get("target");
                target.setFlySpeed(flySpeed);
                if((int) args.get("flyspeed") == 1) {
                    target.sendMessage(Main.get().getPrefix() + "Fly speed has been reset by " + sender.getName());
                    sender.sendMessage(Main.get().getPrefix() + "Reset fly speed of " + target.getName());
                    TeamUtils.sendToTeam(sender.getName(), target.getName(), "Reset the fly speed of " + target.getName());
                } else {
                    target.sendMessage(Main.get().getPrefix() + "Fly speed has been set to " + args.get("flyspeed") + " by " + sender.getName());
                    sender.sendMessage(Main.get().getPrefix() + "Set fly speed of " + target.getName() + " to " + args.get("flyspeed"));
                    TeamUtils.sendToTeam(sender.getName(), target.getName(), "Set the fly speed of " + target.getName() + " to " + args.get("flyspeed"));
                }
            }

        }
    }

}
