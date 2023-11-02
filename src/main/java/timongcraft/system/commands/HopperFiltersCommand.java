package timongcraft.system.commands;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import timongcraft.system.Main;

public class HopperFiltersCommand {

    public static String HOPPER_SORTER_PREFIX = "§6Sorter: ";

    public static void register() {
        new CommandTree("hopperfilter")
                .withShortDescription("A explanation for the hopper sorting system")
                .then(new LiteralArgument("rename")
                        .then(new GreedyStringArgument("name")
                                .executesPlayer(HopperFiltersCommand::hopperFiltersRenamer)))
                .executes(HopperFiltersCommand::hopperFiltersManager)
                .register();
    }

    private static void hopperFiltersManager(CommandSender sender, CommandArguments args) {
        sender.sendMessage(Main.get().getPrefix() + "Hopper Sorting System Usage:");
        sender.sendMessage("To use hopper filters you need to rename a hopper, the following features will help you");
        sender.sendMessage("1. Separate items with commas.");
        sender.sendMessage("2. Use wildcards like * (infinite chars) and ? (as many chars as question marks).");
        sender.sendMessage("3. Use exclamation marks as inverters.");
        sender.sendMessage("Example: '*spruce*,!*boat' will let every spruce item except boats trough");
    }

    private static int hopperFiltersRenamer(Player sender, CommandArguments args) {
        ItemStack item = sender.getInventory().getItemInMainHand();

        if (!item.getType().equals(Material.HOPPER)) {
            sender.sendMessage(Main.get().getPrefix() + "§cYou need to hold a hopper in you hand to rename it.");
            return 0;
        }

        String name = (String) args.get("name");
        ItemMeta itemMeta = item.getItemMeta();

        if (itemMeta == null) {
            sender.sendMessage(Main.get().getPrefix() + "§cNo item meta present!");
            return 0;
        }

        itemMeta.setDisplayName(HOPPER_SORTER_PREFIX + name);
        item.setItemMeta(itemMeta);

        sender.sendMessage(Main.get().getPrefix() + "Renamed hopper");
        return 1;
    }

}
