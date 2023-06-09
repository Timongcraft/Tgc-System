package timongcraft.system.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public record CropDrop(int min, int max, Material material, int dropChance) {
    public ItemStack getRandom() {
        int nextInt = new Random().nextInt(min(), max() + 1);
        return new ItemStack(material(), nextInt);
    }
}
