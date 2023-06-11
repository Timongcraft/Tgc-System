package timongcraft.system.commands;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.executors.CommandArguments;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

public class ColorCodesCommand {
    public static void register() {
        new CommandTree("colorcodes")
                .withShortDescription("Get all color codes as well as the format codes")
                .withUsage("/colorcodes")
                .withPermission("tgc-system.team")
                .executes(ColorCodesCommand::colorCodesManager)
                .register();
    }

    private static int colorCodesManager(CommandSender sender, CommandArguments args) {
        sender.sendMessage(ChatColor.GOLD + "======== Color Codes ========");
        sender.sendMessage("&0 = " + ChatColor.BLACK + "Black" + "             " + ChatColor.WHITE + "&1 = " + ChatColor.DARK_BLUE + "Dark Blue");
        sender.sendMessage("&2 = " + ChatColor.DARK_GREEN + "Dark Green" + "     " + ChatColor.WHITE + "&3 = " + ChatColor.DARK_AQUA + "Dark Aqua");
        sender.sendMessage("&4 = " + ChatColor.DARK_RED + "Dark Red" + "        " + ChatColor.WHITE + "&5 = " + ChatColor.DARK_PURPLE + "Dark Purple");
        sender.sendMessage("&6 = " + ChatColor.GOLD + "Gold" + "              " + ChatColor.WHITE + "&7 = " + ChatColor.GRAY + "Gray");
        sender.sendMessage("&8 = " + ChatColor.DARK_GRAY + "Dark Gray" + "       " + ChatColor.WHITE + "&9 =  " + ChatColor.BLUE + "Blue");
        sender.sendMessage("&a = " + ChatColor.GREEN + "Green" + "            " + ChatColor.WHITE + "&b = " + ChatColor.AQUA + "Aqua");
        sender.sendMessage("&c = " + ChatColor.RED + "Red" + "               " + ChatColor.WHITE + "&d = " + ChatColor.LIGHT_PURPLE + "Light Purple");
        sender.sendMessage("&e = " + ChatColor.YELLOW + "Yellow" + "            " + ChatColor.WHITE + "&f = " + ChatColor.WHITE + "White");
        sender.sendMessage("\n");
        sender.sendMessage(ChatColor.GOLD + "======== Format Codes ========");
        sender.sendMessage("&k = " + ChatColor.MAGIC + "Magic" + ChatColor.WHITE + "             " + "&l = " + ChatColor.BOLD + "Bold");
        sender.sendMessage("&m = " + ChatColor.STRIKETHROUGH + "Strikethrough" + ChatColor.WHITE + "  " + "&n = " + ChatColor.UNDERLINE + "Underline");
        sender.sendMessage("&o = " + ChatColor.ITALIC + "Italic" + ChatColor.WHITE + "             " + "&r = " + ChatColor.RESET + "Reset");
        return 1;
    }
}
