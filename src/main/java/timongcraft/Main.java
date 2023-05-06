package timongcraft;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import timongcraft.commands.*;
import timongcraft.listeners.*;
import timongcraft.util.AutoSave;
import timongcraft.util.DataConfigHandler;

import java.io.File;

public class Main extends JavaPlugin {
    private static Main instance;
    private final String prefix = getConfig().getString("prefix.pluginPrefix");
    private DataConfigHandler dataConfigHandler;
    private boolean firstLoad;

    @Override
    public void onLoad() {
        firstLoad = isFirstLoad();
        if(firstLoad) return;

        getLogger().warning("§cExperimental rewrite build!");

        CommandAPI.onLoad(new CommandAPIBukkitConfig(this).silentLogs(true).missingExecutorImplementationMessage("This command can't be executed with the %s"));
    }

    @Override
    public void onEnable() {
        loadConfigs();
        if(firstLoad) return;

        instance = this;

        double configVersion = getConfig().getDouble("version");
        double version = Double.parseDouble(getDescription().getVersion());
        if(configVersion != version) {
            getLogger().info(ChatColor.RED + "The version of the config.yml does not match with the current plugin version!");
            getLogger().info(ChatColor.RED + "Unless you delete the config and restart the server the plugin will be stopped!");
            getLogger().info(ChatColor.RED + "Do not edit the version in the config.yml or things will break!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        new PluginCommand().disablePluginsOnBoot();

        CommandAPI.onEnable();

        registerCommandsInOnEnable();

        registerEvents();

        if(getConfig().getBoolean("Maintenance.icon")) {
            File maintenanceicon = new File(getDataFolder(), "maintenance-icon.png");
            if(!maintenanceicon.exists()) {
                saveResource("maintenance-icon.png", false); }}

        if(getConfig().getBoolean("autoSave.enabled")) new AutoSave();
    }

    @Override
    public void onDisable() {
        if(firstLoad) return;
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

    private boolean isFirstLoad() {
        return !new File(getDataFolder(), "config.yml").exists();
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