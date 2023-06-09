package timongcraft.system.util;

import timongcraft.system.Main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateCheckHandler {
    public static void checkForUpdate(double currentVersion) {
        try {
            URL url = new URL("https://api.modrinth.com/v2/project/J8w9otm3/version");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            if (responseCode != 200) {
                Main.get().getLogger().warning("Failed to check for updates. Response code: " + responseCode);
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            String jsonString = response.toString();
            int versionNumberIndex = jsonString.indexOf("\"version_number\":");
            int startIndex = jsonString.indexOf('"', versionNumberIndex + "\"version_number\":".length()) + 1;
            int endIndex = jsonString.indexOf('"', startIndex);
            double latestVersion = Double.parseDouble(jsonString.substring(startIndex, endIndex));

            if(latestVersion != currentVersion) {
                Main.get().getLogger().warning("You're not using the latest version!");
                Main.get().getLogger().warning("Get the latest version here: https://modrinth.com/plugin/tgc-system");
            }

        } catch (Exception e) {
            Main.get().getLogger().warning("Failed to check for updates");
            e.printStackTrace();
        }
    }
}