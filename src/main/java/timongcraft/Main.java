package timongcraft;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import timongcraft.commands.*;
import timongcraft.listeners.*;
import timongcraft.util.AutoSaveHandler;
import timongcraft.util.DataConfigHandler;
import timongcraft.util.UpdateCheckHandler;

import java.io.File;

public class Main extends JavaPlugin {
    private static Main instance;
    private final String prefix = getConfig().getString("prefix.pluginPrefix");
    private DataConfigHandler dataConfigHandler;
    private AutoSaveHandler autoSaveHandler;
    private boolean noLoad;

    @Override
    public void onLoad() {
        noLoad = !new File(getDataFolder(), "config.yml").exists();
        if(noLoad) return;

        CommandAPI.onLoad(new CommandAPIBukkitConfig(this).silentLogs(true).missingExecutorImplementationMessage("This command can't be executed with the %s"));
    }

    @Override
    public void onEnable() {
        loadConfigs();
        if(noLoad) return;

        instance = this;

        configVersionCheck();
        if(noLoad) return;

        PluginCommand.disablePluginsOnBoot();

        if(getConfig().getBoolean("newUpdateNotifications.console")) {
            UpdateCheckHandler.checkForUpdate(Double.parseDouble(getDescription().getVersion()));
        }

        CommandAPI.onEnable();

        registerCommandsInOnEnable();

        registerEvents();

        enableAutoSave();
    }

    @Override
    public void onDisable() {
        if(noLoad) return;

        if(getConfig().getBoolean("autoSave.enabled")) autoSaveHandler.cancel();
    }

    private void configVersionCheck() {
        double configVersion = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml")).getDouble("configVersion");
        if(configVersion != 1.5) {
            getLogger().info("§cThe version of the config.yml does not match with the current plugin version!");
            getLogger().info("§cUnless you delete the config and restart the server the plugin will be stopped!");
            getLogger().info("§cDo not edit the version in the config.yml or things will break!");
            noLoad = true;
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    private void registerCommandsInOnEnable() {
        AlertCommand.register();
        ColorCodesCommand.register();
        FlySpeedCommand.register();
        if(Main.get().getConfig().getBoolean("chatSystem.enabled")) {
            MsgCommand.register();
            ReplyCommand.register();
        }
        MaintenanceCommand.register();
        if(Main.get().getConfig().getBoolean("permissionSystem.enabled")) {
            PermissionManagerCommand.register();
        }
        PluginCommand.register();
        RebootCommand.register();
        ReloadConfigsCommand.register();
        ResourcePackCommand.register();
        if(Main.get().getConfig().getBoolean("statuses.enabled")) {
            PermissionManagerCommand.register();
        }
        StatusCommand.register();
    }

    private void registerEvents() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new BlockCommandsListeners(), this);
        pluginManager.registerEvents(new ConnectionListeners(), this);
        pluginManager.registerEvents(new OtherListeners(), this);
        if(!getConfig().getStringList("blockedCommands").isEmpty() || !getConfig().getStringList("blockedPrefix").isEmpty()) {
            pluginManager.registerEvents(new BlockCommandsListeners(), this);
        }
        if(getConfig().getBoolean("spawnElytra.enabled")) {
            pluginManager.registerEvents(new SpawnElytraListeners(), this);
        }

    }

    private void enableAutoSave() {
        autoSaveHandler = new AutoSaveHandler();
        if(getConfig().getBoolean("autoSave.enabled")) autoSaveHandler.runTaskTimer(this, autoSaveHandler.parseInterval(Main.get().getConfig().getString("autoSave.time")), autoSaveHandler.parseInterval(Main.get().getConfig().getString("autoSave.time")));
    }

    private void loadConfigs() {
        if(!new File(getDataFolder(), "config.yml").exists()) {
            saveDefaultConfig();
            new DataConfigHandler(new File(getDataFolder(), "data.yml")).save();
            getLogger().warning("Loaded for the first time, please set the config values!");
            getLogger().warning("Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
        } else {
            reloadConfig();
            dataConfigHandler = new DataConfigHandler(new File(getDataFolder(), "data.yml"));
        }
    }

    public static Main get() {
        return instance;
    }

    public String getPrefix() {
        return prefix;
    }

    public DataConfigHandler getDataConfig() { return dataConfigHandler; }
}