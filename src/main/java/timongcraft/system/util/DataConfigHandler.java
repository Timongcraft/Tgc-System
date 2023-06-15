package timongcraft.system.util;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import timongcraft.system.Main;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class DataConfigHandler extends YamlConfiguration {
    private final File file;

    public DataConfigHandler(File file) {
        this.file = file;
        load();
    }

    public void load() {
        try {
            checkIfExists();
            this.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            Main.get().getLogger().log(Level.SEVERE, "The data.yml could not be loaded!", e);
        }
    }

    public void save() {
        try {
            checkIfExists();
            this.save(file);
        } catch (IOException e) {
            Main.get().getLogger().log(Level.SEVERE, "The data.yml could not be saved!", e);
        }
    }

    public void checkIfExists() throws IOException {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
    }
}