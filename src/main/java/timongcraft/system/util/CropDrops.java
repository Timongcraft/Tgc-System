package timongcraft.system.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class CropDrops {

    private final List<CropDrop> drops;

    public CropDrops(CropDrop... drops) {
        this.drops = Arrays.asList(drops);
    }

    public List<CropDrop> getRandoms() {
        return drops.stream().filter((cropDrop -> new Random().nextInt(0, 101) <= cropDrop.dropChance()))
                .collect(Collectors.toList());
    }

    public record CropDrop(int min, int max, Material material, int dropChance) {
        public ItemStack getRandom() {
            int nextInt = new Random().nextInt(min(), max() + 1);
            return new ItemStack(material(), nextInt);
        }
    }

}
