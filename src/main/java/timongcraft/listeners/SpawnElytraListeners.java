package timongcraft.listeners;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.KeybindComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import timongcraft.Main;

import java.util.ArrayList;
import java.util.List;

public class SpawnElytraListeners implements Listener {
    private final List<Player> flying = new ArrayList<>();
    private final List<Player> boosted = new ArrayList<>();

    public SpawnElytraListeners() {
        Bukkit.getScheduler().runTaskTimer(Main.get(), () -> {
            for(Player playerInWorld : Bukkit.getWorld(String.valueOf(Main.get().getConfig().getString("spawnElytra.worldName"))).getPlayers()) {
                if(playerInWorld.getGameMode() == GameMode.SPECTATOR || playerInWorld.getGameMode() == GameMode.CREATIVE) return;
                playerInWorld.setAllowFlight(isInSpawnRadius(playerInWorld));
                if(flying.contains(playerInWorld) && !playerInWorld.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().isAir()) {
                    playerInWorld.setAllowFlight(false);
                    playerInWorld.setFlying(false);
                    playerInWorld.setGliding(false);
                    boosted.remove(playerInWorld);
                    Bukkit.getScheduler().runTaskLater(Main.get(), () -> {
                        flying.remove(playerInWorld);
                    },5);
                }
            }
        }, 0, 4);

    }

    @EventHandler
    public void onDoubleJump(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if(player.getGameMode() == GameMode.SPECTATOR || player.getGameMode() == GameMode.CREATIVE) return;
        event.setCancelled(true);
        player.setGliding(true);
        flying.add(player);
        if(Main.get().getConfig().getBoolean("spawnElytra.boost.enabled")) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder("Press ")
                    .append(new KeybindComponent("key.swapOffhand"))
                    .append(" to boost")
                    .create());
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if(event.getEntityType() == EntityType.PLAYER
        && (event.getCause() == EntityDamageEvent.DamageCause.FALL
                || event.getCause() == EntityDamageEvent.DamageCause.FLY_INTO_WALL)
        && flying.contains(event.getEntity())) event.setCancelled(true);
    }

    @EventHandler
    public void onSwapItem(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if(boosted.contains(player) || !Main.get().getConfig().getBoolean("spawnElytra.boost.enabled")) return;
        event.setCancelled(true);
        boosted.add(player);

        for(Player playerInWorld : Bukkit.getWorld(String.valueOf(Main.get().getConfig().getString("spawnElytra.worldName"))).getPlayers()) {
            if(flying.contains(playerInWorld) && playerInWorld.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().isAir()) player.setVelocity(player.getLocation().getDirection().multiply(Main.get().getConfig().getInt("spawnElytra.boost.enabled.multiplyValue")));
        }
    }

    @EventHandler
    public void onToggleGlide(EntityToggleGlideEvent event) {
        if(event.getEntityType() == EntityType.PLAYER && flying.contains(event.getEntity())) event.setCancelled(true);
    }

    private Boolean isInSpawnRadius(Player player) {
        if(!player.getWorld().getName().equals(Main.get().getConfig().getString("spawnElytra.worldName"))) return false;
        return player.getWorld().getSpawnLocation().distance(player.getLocation()) < Main.get().getConfig().getInt("spawnElytra.spawnRadius");
    }
}
