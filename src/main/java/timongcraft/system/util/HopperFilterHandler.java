package timongcraft.system.util;

import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import timongcraft.system.commands.HopperFiltersCommand;

import java.util.HashMap;
import java.util.regex.Pattern;

public class HopperFilterHandler implements Listener {
    HashMap<String, Pattern> patternCache;

    public HopperFilterHandler() {
        patternCache = new HashMap<>();
    }

    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        if (!event.getDestination().getType().equals(InventoryType.HOPPER)) return;
        if (!(event.getDestination().getHolder() instanceof Container container)) return;
        String filter = container.getCustomName();
        if (filter == null || !filter.startsWith(HopperFiltersCommand.HOPPER_SORTER_PREFIX)) return;
        filter = filter.substring(HopperFiltersCommand.HOPPER_SORTER_PREFIX.length());
        if (filterMatch(filter, event.getItem().getType().getKey().getKey())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryPickupItem(InventoryPickupItemEvent event) {
        if (!event.getInventory().getType().equals(InventoryType.HOPPER)) return;
        if (!(event.getInventory().getHolder() instanceof Container container)) return;
        String filter = container.getCustomName();
        if (filter == null || !filter.startsWith(HopperFiltersCommand.HOPPER_SORTER_PREFIX)) return;
        filter = filter.substring(HopperFiltersCommand.HOPPER_SORTER_PREFIX.length());
        if (filterMatch(filter, event.getItem().getItemStack().getType().getKey().getKey())) return;
        event.setCancelled(true);
    }

    private boolean filterMatch(String filterString, String fullItemName) {
        String[] sections = filterString.split(",");

        boolean matchesPositive = false;
        boolean matchesNegative = false;
        for (String section : sections) {
            boolean negative = section.startsWith("!");
            if ((negative && matchesNegative) || (!negative && matchesPositive)) break;
            if (negative) section = section.substring(1);
            boolean match = fullItemName.matches("^" + section.replace("*", ".*").replace("?", ".") + "$");
            if (negative) matchesNegative = match;
            else matchesPositive = match;
        }

        return matchesPositive && !matchesNegative;
    }
}