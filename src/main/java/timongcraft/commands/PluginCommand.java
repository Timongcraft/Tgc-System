package timongcraft.commands;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.executors.CommandExecutor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import timongcraft.Main;

import java.util.ArrayList;
import java.util.List;

public class PluginCommand {
    public static void register() {
        new CommandTree("plugin")
                .withFullDescription("Turn Plugins on/off")
                .withPermission("tgc-system.team")
                .then(new LiteralArgument("enable")
                        .then(new StringArgument("plugin")
                                .replaceSuggestions(ArgumentSuggestions.strings(info -> getDisabledPluginsList()))
                                .executes(new PluginEnableExecutor())))
                .then(new LiteralArgument("disable")
                        .then(new StringArgument("plugin")
                                .replaceSuggestions(ArgumentSuggestions.strings(info -> getEnabledPluginsList()))
                                .executes(new PluginDisableExecutor())))
                .register();
    }

    private static String[] getDisabledPluginsList() {
        List<String> plugins = new ArrayList<>();
        List<String> disabledPlugins = Main.get().getDataConfig().getStringList("disabledPlugins");
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if(!plugin.getName().equalsIgnoreCase(Main.get().getName())) {
                if((disabledPlugins.contains(plugin.getName()))) {
                    plugins.add(plugin.getName());
                }
            }
        }
        return plugins.toArray(new String[0]);
    }

    private static String[] getEnabledPluginsList() {
        List<String> plugins = new ArrayList<>();
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if(!plugin.getName().equalsIgnoreCase(Main.get().getName())) {
                if(plugin.isEnabled()) {
                    plugins.add(plugin.getName());
                }
            }
        }
        return plugins.toArray(new String[0]);
    }

    private static class PluginEnableExecutor implements CommandExecutor {
        @Override
        public void run(CommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException {
            String pluginName = (String) args.get("plugin");
            
            Plugin targetPlugin = Main.get().getServer().getPluginManager().getPlugin(pluginName);
            if(targetPlugin == null) {
                sender.sendMessage(Main.get().getPrefix() + "§cPlugin " + pluginName + " not found.");
                return;
            }

            if(!Main.get().getDataConfig().isSet("disabledPlugins")) {
                sender.sendMessage(Main.get().getPrefix() + "§cPlugin " + pluginName + " can't be enabled.");
                return;
            }

            List<String> disabledPlugins = Main.get().getDataConfig().getStringList("disabledPlugins");
            if(disabledPlugins.contains(pluginName)) {
                Main.get().getServer().getPluginManager().enablePlugin(targetPlugin);
                disabledPlugins.remove(pluginName);
                Main.get().getDataConfig().set("disabledPlugins", disabledPlugins);
                Main.get().getDataConfig().save();
                sender.sendMessage(Main.get().getPrefix() + pluginName + " has been enabled.");
            } else {
                sender.sendMessage(Main.get().getPrefix() + "§c" + pluginName + " is not disabled.");
            }
        }
    }

    private static class PluginDisableExecutor implements CommandExecutor {
        @Override
        public void run(CommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException {
            String pluginName = (String) args.get("plugin");

            Plugin targetPlugin = Main.get().getServer().getPluginManager().getPlugin(pluginName);
            if(targetPlugin == null) {
                sender.sendMessage(Main.get().getPrefix() + "§cPlugin " + pluginName + " not found.");
                return;
            }
            if(targetPlugin.getName().equalsIgnoreCase(Main.get().getName())) {
                sender.sendMessage(Main.get().getPrefix() + "§cThis plugin can't be disabled.");
                return;
            }
            
            Main.get().getServer().getPluginManager().disablePlugin(targetPlugin);
            if(!Main.get().getDataConfig().isSet("disabledPlugins")) {
                Main.get().getDataConfig().set("disabledPlugins", new ArrayList<>());
            }
            List<String> disabledPlugins = Main.get().getDataConfig().getStringList("disabledPlugins");
            disabledPlugins.add(pluginName);
            Main.get().getDataConfig().set("disabledPlugins", disabledPlugins);
            Main.get().getDataConfig().save();
            sender.sendMessage(Main.get().getPrefix() + pluginName + " has been disabled.");
        }
    }

    public void disablePluginsOnBoot() {
        List<String> disabledPlugins = Main.get().getDataConfig().getStringList("disabledPlugins");
        if(disabledPlugins.isEmpty()) {
            return;
        }

        PluginManager pluginManager = Main.get().getServer().getPluginManager();
        for (String pluginName : disabledPlugins) {
            Plugin targetPlugin = pluginManager.getPlugin(pluginName);
            if(targetPlugin != null) {
                Bukkit.getScheduler().runTaskLater(Main.get(), () -> {
                    Main.get().getLogger().info("Disabling " + targetPlugin.getName());
                    pluginManager.disablePlugin(targetPlugin);
                },200);
            }
        }
    }
}
