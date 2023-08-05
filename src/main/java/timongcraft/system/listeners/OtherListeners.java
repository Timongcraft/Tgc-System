package timongcraft.system.listeners;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import timongcraft.system.Main;
import timongcraft.system.util.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OtherListeners implements Listener {
    private static final Pattern urlPattern = Pattern.compile("^(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?$");

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return;
        if (!Main.get().getConfig().getBoolean("chatSystem.enabled")) return;

        Player player = event.getPlayer();
        TextComponent playerChatName;
        String rawMessage = event.getMessage();
        TextComponent message = new TextComponent();

        if (!player.hasPermission("tgc-system.team") && Main.get().getConfig().getBoolean("chatSystem.noLinks")) {
            for (String messagePart : rawMessage.split("\\s+")) {
                Matcher matcher = urlPattern.matcher(messagePart);
                if (matcher.matches()) {
                    player.sendMessage(Main.get().getPrefix() + "§cYou don't have the permission to send links!");
                    event.setCancelled(true);
                    return;
                }
            }
        }

        if (Main.get().getConfig().getBoolean("statuses.enabled")) {
            if (player.hasPermission("tgc-system.team")) {
                playerChatName = new TextComponent("§4<§c" + new PlayerUtils().getPlayerNameWithStatus(player, true) + "§4>§f ");
            } else
                playerChatName = new TextComponent("§8<§7" + new PlayerUtils().getPlayerNameWithStatus(player, true) + "§8>§7 ");
        } else {
            if (player.hasPermission("tgc-system.team")) {
                playerChatName = new TextComponent("§4<§c" + new PlayerUtils().getPlayerNameWithStatus(player, false) + "§4>§f ");
            } else
                playerChatName = new TextComponent("§f<" + new PlayerUtils().getPlayerNameWithStatus(player, false) + "§f> ");
        }

        if (player.hasPermission("tgc-system.team")) {
            rawMessage = rawMessage.replaceAll("&", "§");
        }

        if (Main.get().getConfig().getBoolean("coordsSaver.enabled")) {
            if (Main.get().getConfig().getBoolean("coordsSaver.xaerosWaypointCompatability")) {
                TextComponent xaerosWaypoint = CoordsMessageUtils.getXaerosWaypointAsClickableCoordinatesMessage(rawMessage);

                if (xaerosWaypoint != null) {
                    message.addExtra(xaerosWaypoint);
                } else {
                    message.addExtra(CoordsMessageUtils.getAsClickableCoordinatesMessage(event.getPlayer(), rawMessage, true));
                }
            } else {
                message.addExtra(CoordsMessageUtils.getAsClickableCoordinatesMessage(event.getPlayer(), rawMessage, false));
            }
        }

        if (player.hasPermission("tgc-system.team") && rawMessage.startsWith(Main.get().getConfig().getString("prefix.teamChatPrefixInChat"))) {
            for (Player teamPlayer : Bukkit.getOnlinePlayers()) {
                if (teamPlayer.hasPermission("tgc-system.team")) {
                    teamPlayer.spigot().sendMessage(new TextComponent(Main.get().getConfig().getString("prefix.teamChatPrefix")), message);
                }
            }
            event.setCancelled(true);
            return;
        }

        String legacyMessage = message.toLegacyText();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            TextComponent modifiedMessage = message.duplicate();

            if (legacyMessage.contains(onlinePlayer.getName())) {
                ChatColor highlightColor = (onlinePlayer.hasPermission("tgc-system.team") && (player.hasPermission("tgc-system.team"))) ? ChatColor.YELLOW : ChatColor.WHITE;
                ComponentUtils.findAllTextComponents(modifiedMessage, textComponent -> {
                    textComponent.setText(textComponent.getText().replaceAll(onlinePlayer.getName(), highlightColor + onlinePlayer.getName() + ChatColor.RESET));
                });

                if (highlightColor == ChatColor.YELLOW) {
                    onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1.0F, 2.0F);
                }
            }

            if (Main.get().getConfig().getBoolean("chatSystem.timeStampInChat.enabled")) {
                TextComponent timeStamp = new TextComponent("§7[" + DateTimeFormatter.ofPattern(Main.get().getConfig().getString("chatSystem.timeStampInChat.format")).format(ZonedDateTime.now(ZoneId.of(Main.get().getConfig().getString("chatSystem.timeStampInChat.timeZone")))) + "] §f");

                onlinePlayer.spigot().sendMessage(timeStamp, playerChatName, modifiedMessage);
            } else {
                onlinePlayer.spigot().sendMessage(playerChatName, modifiedMessage);
            }
        }

        Bukkit.getLogger().info("Chat: " + ChatColor.stripColor("<" + player.getName() + "> " + legacyMessage));

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerCropInteract(PlayerInteractEvent event) {
        if (!Main.get().getConfig().getBoolean("easyHarvest.enabled")) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        if (block == null) return;
        final Map<Material, CropDrops> cropDrops = Map.of(
                Material.WHEAT, new CropDrops(new CropDrop(1, 1, Material.WHEAT, 100), new CropDrop(1, 3, Material.WHEAT_SEEDS, 100)),
                Material.POTATOES, new CropDrops(new CropDrop(2, 4, Material.POTATO, 100), new CropDrop(1, 1, Material.POISONOUS_POTATO, 2)),
                Material.CARROTS, new CropDrops(new CropDrop(1, 4, Material.CARROT, 100)),
                Material.BEETROOTS, new CropDrops(new CropDrop(1, 1, Material.BEETROOT, 100), new CropDrop(1, 3, Material.BEETROOT_SEEDS, 100)),
                Material.COCOA, new CropDrops(new CropDrop(2, 2, Material.COCOA_BEANS, 100))
        );
        if (!cropDrops.containsKey(block.getType())) return;

        Player player = event.getPlayer();
        if (player.isSneaking()) return;

        Ageable ageable = (Ageable) block.getBlockData();
        if (ageable.getAge() != ageable.getMaximumAge()) return;

        CropDrops drops = cropDrops.get(block.getType());
        if (drops == null) return;

        ageable.setAge(0);
        block.setBlockData(ageable);
        player.swingMainHand();

        for (CropDrop cropDrop : drops.getRandoms()) {
            block.getWorld().dropItemNaturally(block.getLocation(), cropDrop.getRandom());
        }
        player.playSound(player.getLocation(), Sound.BLOCK_CROP_BREAK, 1.0f, 1.0f);
        event.setUseItemInHand(Event.Result.DENY);
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent event) {
        if (event.getBlock().getLocation().getBlockY() < 0) {
            if (!Main.get().getConfig().getBoolean("deepslateGenerator.enabled")) return;
            Material newMaterial = switch (event.getNewState().getType()) {
                case COBBLESTONE -> Material.COBBLED_DEEPSLATE;
                case STONE -> Material.DEEPSLATE;
                default -> null;
            };

            if (newMaterial == null) return;
            event.getNewState().setType(newMaterial);
        }
    }

    @EventHandler
    public void onPlayerResourcePackStatus(PlayerResourcePackStatusEvent event) {
        Player player = event.getPlayer();
        if (!Main.get().getConfig().getBoolean("resourcePack.force")) return;
        if (player.hasPermission("tgc-system.team")) return;
        PlayerResourcePackStatusEvent.Status status = event.getStatus();

        if (status == PlayerResourcePackStatusEvent.Status.DECLINED || status == PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD) {
            player.kickPlayer("§cYou must use the server's resource pack.");
        }

        if (status == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED) {
            ConnectionListeners.resourcePackLoaded.add(player.getUniqueId());
        }
    }

}