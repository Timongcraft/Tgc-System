package timongcraft.system.util;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class PlayerOnlyArgument extends EntitySelectorArgument.OnePlayer {

    public PlayerOnlyArgument(String nodeName) {
        super(nodeName);
        replaceSuggestions(ArgumentSuggestions.strings(info ->
                Bukkit.getOnlinePlayers().stream().map(OfflinePlayer::getName).toArray(String[]::new)
        ));
    }

}