package com.black.minecraft.b0rain.rain;

import com.black.minecraft.b0rain.B0rain;
import com.black.minecraft.b0rain.config.ConfigManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class ElytraHandler implements Listener {
    private final B0rain plugin;
    private final ConfigManager configManager;
    private final NotificationManager notificationManager;
    private final SoundManager soundManager;

    public ElytraHandler(B0rain plugin, ConfigManager configManager, 
                         NotificationManager notificationManager, SoundManager soundManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.notificationManager = notificationManager;
        this.soundManager = soundManager;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onToggleGlide(EntityToggleGlideEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (!event.isGliding()) {
            return;
        }

        if (!shouldBlockElytra(player)) {
            return;
        }

        String mode = configManager.getElytraMode();
        if (mode.equals("disable")) {
            event.setCancelled(true);
            notifyAndSound(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!player.isGliding()) {
            return;
        }

        if (!shouldBlockElytra(player)) {
            return;
        }

        String mode = configManager.getElytraMode();

        if (mode.equals("disable")) {
            player.setGliding(false);
            notifyAndSound(player);
        } else if (mode.equals("slow")) {
            applySlowdown(player);
        }
    }

    private boolean shouldBlockElytra(Player player) {
        if (!configManager.isElytraEnabled()) {
            return false;
        }

        if (player.hasPermission("wfx.elytra.bypass")) {
            return false;
        }

        if (!configManager.isWorldAllowed(player.getWorld().getName())) {
            return false;
        }

        if (!player.getWorld().hasStorm() && !player.getWorld().isThundering()) {
            return false;
        }

        String biome = player.getLocation().getBlock().getBiome().getKey().toString();
        if (!configManager.isBiomeAllowed(biome)) {
            return false;
        }

        return isUnderOpenSky(player);
    }

    private boolean isUnderOpenSky(Player player) {
        Location loc = player.getLocation();
        int highestY = player.getWorld().getHighestBlockYAt(loc.getBlockX(), loc.getBlockZ());
        return loc.getY() >= highestY;
    }

    private void applySlowdown(Player player) {
        double multiplier = configManager.getElytraSlowMultiplier();
        Vector velocity = player.getVelocity();
        player.setVelocity(velocity.multiply(multiplier));
    }

    private void notifyAndSound(Player player) {
        notificationManager.notifyElytraBlocked(player);
        soundManager.playElytraBlockSound(player);
    }
}
