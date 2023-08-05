package timongcraft.system.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import timongcraft.system.Main;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class CommandAPILoader {
    public static void load(String version, boolean noConfig) throws IOException, InvalidPluginException, InvalidDescriptionException {
        try (Stream<Path> pathStream = Files.list(Path.of("plugins"))) {
            if (pathStream.anyMatch(a -> {
                if (!((a.getFileName().toString().contains("CommandAPI") || a.getFileName().toString().contains("commandapi")) && a.getFileName().toString().endsWith(".jar")))
                    return false;

                if (!a.getFileName().toString().equals("CommandAPI-" + version + ".jar")) {
                    if (noConfig || Main.get().getConfig().getBoolean("CommandAPI.warnings")) {
                        Main.get().getLogger().warning("You may have loaded an incompatible version of CommandAPI. For best compatibility, use version " + version + ".");
                    }
                }
                return true;
            })) return;
        }

        Main.get().getLogger().info("Required library CommandAPI not found, loading compatible version automatically...");

        URL url = new URL("https://github.com/JorelAli/CommandAPI/releases/download/" + version + "/CommandAPI-" + version + ".jar");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();
        if (responseCode != 200) {
            Main.get().getLogger().warning("Failed to check for updates. Response code: " + responseCode);
        }

        Path commandAPIPath = Path.of("plugins/CommandAPI-" + version + ".jar");

        Files.copy(con.getInputStream(), commandAPIPath);
        con.disconnect();
        Bukkit.getPluginManager().loadPlugin(commandAPIPath.toFile());
    }
}
