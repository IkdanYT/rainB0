package com.black.minecraft.b0rain.rain;

import com.black.minecraft.b0rain.B0rain;
import com.black.minecraft.b0rain.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RainEffectManager implements Listener {
    private final B0rain plugin;
    private final ConfigManager configManager;
    private BukkitRunnable task;
    private final Set<UUID> playersWithPluginEffect;
    private final Set<UUID> disabledPlayers;
    private PotionEffectType slownessType;
    private PotionEffect slownessEffect;

    public RainEffectManager(B0rain plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.playersWithPluginEffect = new HashSet<>();
        this.disabledPlayers = new HashSet<>();
        initEffects();
    }

    private void initEffects() {
        this.slownessType = PotionEffectHelper.getSlownessType();
        this.slownessEffect = PotionEffectHelper.createSlownessEffect(
                configManager.getSlownessDurationTicks(),
                configManager.getSlownessLevel()
        );
        if (slownessType == null) {
            plugin.getLogger().severe("Failed to load SLOWNESS potion effect type!");
        }
    }

    public void start() {
        if (task != null) {
            task.cancel();
        }

        initEffects();
        Bukkit.getPluginManager().registerEvents(this, plugin);

        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (slownessType == null || slownessEffect == null) {
                    return;
                }

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!configManager.isWorldEnabled(player.getWorld().getName())) {
                        continue;
                    }

                    UUID playerId = player.getUniqueId();

                    if (player.hasPermission("rainb0.bypass") || disabledPlayers.contains(playerId)) {
                        if (playersWithPluginEffect.contains(playerId)) {
                            player.removePotionEffect(slownessType);
                            playersWithPluginEffect.remove(playerId);
                        }
                        continue;
                    }

                    boolean inRain = RainChecker.isInRain(
                            player,
                            configManager.isCheckThunder(),
                            configManager.isCheckSnow(),
                            configManager.getIgnoredBiomes()
                    );

                    if (inRain) {
                        player.addPotionEffect(slownessEffect);
                        playersWithPluginEffect.add(playerId);
                    } else {
                        if (playersWithPluginEffect.contains(playerId)) {
                            player.removePotionEffect(slownessType);
                            playersWithPluginEffect.remove(playerId);
                        }
                    }
                }
            }
        };

        task.runTaskTimer(plugin, 0L, configManager.getCheckIntervalTicks());
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }

        if (slownessType != null) {
            for (UUID playerId : new HashSet<>(playersWithPluginEffect)) {
                Player player = Bukkit.getPlayer(playerId);
                if (player != null) {
                    player.removePotionEffect(slownessType);
                }
            }
            playersWithPluginEffect.clear();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        playersWithPluginEffect.remove(playerId);
        disabledPlayers.remove(playerId);
    }

    public boolean togglePlayer(UUID playerId) {
        if (disabledPlayers.contains(playerId)) {
            disabledPlayers.remove(playerId);
            return true;
        } else {
            disabledPlayers.add(playerId);
            Player player = Bukkit.getPlayer(playerId);
            if (player != null && slownessType != null && playersWithPluginEffect.contains(playerId)) {
                player.removePotionEffect(slownessType);
                playersWithPluginEffect.remove(playerId);
            }
            return false;
        }
    }

    public boolean isDisabled(UUID playerId) {
        return disabledPlayers.contains(playerId);
    }

    public int getAffectedCount() {
        return playersWithPluginEffect.size();
    }

    public Set<UUID> getAffectedPlayers() {
        return new HashSet<>(playersWithPluginEffect);
    }
}
