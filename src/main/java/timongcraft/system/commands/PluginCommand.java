package timongcraft.system.commands;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import jdk.jfr.Description;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import timongcraft.system.Main;

import java.util.ArrayList;
import java.util.List;

@Deprecated(forRemoval = true)
@Description("Plugin loading and unloading can cause unexpected failures for unfamiliar users")
public class PluginCommand {

    public static void register() {
        new CommandTree("plugin")
                .withShortDescription("Toggle plugins")
                .withUsage("/plugin <enable|disable>")
                .withPermission("tgc-system.team")
                .then(new LiteralArgument("enable")
                        .then(new StringArgument("plugin")
                                .replaceSuggestions(ArgumentSuggestions.strings(info -> getDisabledPlugins()))
                                .executes(PluginCommand::pluginEnableManager)))
                .then(new LiteralArgument("disable")
                        .then(new StringArgument("plugin")
                                .replaceSuggestions(ArgumentSuggestions.strings(info -> getEnabledPlugins()))
                                .executes(PluginCommand::pluginDisableManager)))
                .register();
    }

    private static String[] getDisabledPlugins() {
        List<String> plugins = new ArrayList<>();
        List<String> disabledPlugins = Main.get().getDataConfig().getStringList("disabledPlugins");
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin.getName().equalsIgnoreCase(Main.get().getName())) continue;
            if (!(disabledPlugins.contains(plugin.getName()))) continue;
            plugins.add(plugin.getName());
        }
        return plugins.toArray(new String[0]);
    }

    private static String[] getEnabledPlugins() {
        List<String> plugins = new ArrayList<>();
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin.getName().equalsIgnoreCase(Main.get().getName())) continue;
            if (!plugin.isEnabled()) continue;
            plugins.add(plugin.getName());
        }
        return plugins.toArray(new String[0]);
    }

    private static int pluginEnableManager(CommandSender sender, CommandArguments args) {
        String pluginName = (String) args.get("plugin");

        Plugin targetPlugin = Main.get().getServer().getPluginManager().getPlugin(pluginName);
        if (targetPlugin == null) {
            sender.sendMessage(Main.get().getPrefix() + "§c" + pluginName + " not found.");
            return 0;
        }

        if (!Main.get().getDataConfig().isSet("disabledPlugins") || Main.get().getDataConfig().getStringList("disabledPlugins").isEmpty()) {
            sender.sendMessage(Main.get().getPrefix() + "§c" + pluginName + " can't be enabled.");
            return 0;
        }

        List<String> disabledPlugins = Main.get().getDataConfig().getStringList("disabledPlugins");
        if (disabledPlugins.contains(pluginName)) {
            Main.get().getServer().getPluginManager().enablePlugin(targetPlugin);
            disabledPlugins.remove(pluginName);
            Main.get().getDataConfig().set("disabledPlugins", disabledPlugins);
            Main.get().getDataConfig().save();
            sender.sendMessage(Main.get().getPrefix() + pluginName + " has been enabled.");
        } else {
            sender.sendMessage(Main.get().getPrefix() + "§c" + pluginName + " is not disabled.");
        }
        return 1;
    }

    private static int pluginDisableManager(CommandSender sender, CommandArguments args) {
        String pluginName = (String) args.get("plugin");

        Plugin targetPlugin = Main.get().getServer().getPluginManager().getPlugin(pluginName);
        if (targetPlugin == null) {
            sender.sendMessage(Main.get().getPrefix() + "§c" + pluginName + " not found.");
            return 0;
        }

        if (targetPlugin.getName().equals(Main.get().getName())) {
            sender.sendMessage(Main.get().getPrefix() + "§cThis plugin can't be disabled.");
            return 0;
        }

        Main.get().getServer().getPluginManager().disablePlugin(targetPlugin);
        if (!Main.get().getDataConfig().isSet("disabledPlugins"))
            Main.get().getDataConfig().set("disabledPlugins", new ArrayList<>());
        List<String> disabledPlugins = Main.get().getDataConfig().getStringList("disabledPlugins");
        disabledPlugins.add(pluginName);
        Main.get().getDataConfig().set("disabledPlugins", disabledPlugins);
        Main.get().getDataConfig().save();
        sender.sendMessage(Main.get().getPrefix() + pluginName + " has been disabled.");
        return 1;
    }

    public static void disablePluginsOnBoot() {
        List<String> disabledPlugins = Main.get().getDataConfig().getStringList("disabledPlugins");
        if (disabledPlugins.isEmpty()) return;

        PluginManager pluginManager = Main.get().getServer().getPluginManager();
        for (String pluginName : disabledPlugins) {
            Plugin targetPlugin = pluginManager.getPlugin(pluginName);
            if (targetPlugin == null) continue;

            Bukkit.getScheduler().runTaskLater(Main.get(), () -> {
                Main.get().getLogger().info("Disabling " + targetPlugin.getName());
                pluginManager.disablePlugin(targetPlugin);
            }, 200);
        }
    }

}
