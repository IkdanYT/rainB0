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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class RainEffectManager implements Listener {
    private final B0rain plugin;
    private final ConfigManager configManager;
    private BukkitRunnable task;
    private final Set<UUID> disabledPlayers;
    private final Map<UUID, PotionEffectType> activeEffects;

    public RainEffectManager(B0rain plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.disabledPlayers = new HashSet<>();
        this.activeEffects = new HashMap<>();
    }

    public void start() {
        if (task != null) {
            task.cancel();
        }

        Bukkit.getPluginManager().registerEvents(this, plugin);

        task = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    processPlayer(player);
                }
            }
        };

        task.runTaskTimer(plugin, 0L, configManager.getCheckIntervalTicks());
    }

    private void processPlayer(Player player) {
        UUID playerId = player.getUniqueId();

        if (player.hasPermission("rainb0.bypass") || disabledPlayers.contains(playerId)) {
            removeEffect(player);
            return;
        }

        if (!RainChecker.shouldApplyEffect(player, configManager)) {
            removeEffect(player);
            return;
        }

        WeatherType weather = RainChecker.getWeatherType(player, configManager);
        PotionEffect effect = getEffectForWeather(weather);

        if (effect == null) {
            removeEffect(player);
            return;
        }

        player.addPotionEffect(effect);
        activeEffects.put(playerId, effect.getType());
    }

    private PotionEffect getEffectForWeather(WeatherType weather) {
        return switch (weather) {
            case RAIN -> configManager.getRainEffect();
            case THUNDER -> configManager.getThunderEffect();
            case SNOW -> configManager.getSnowEffect();
            default -> null;
        };
    }

    private void removeEffect(Player player) {
        UUID playerId = player.getUniqueId();
        PotionEffectType type = activeEffects.remove(playerId);
        if (type != null) {
            player.removePotionEffect(type);
        }
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }

        for (UUID playerId : new HashSet<>(activeEffects.keySet())) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                removeEffect(player);
            }
        }
        activeEffects.clear();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        activeEffects.remove(playerId);
        disabledPlayers.remove(playerId);
    }

    public boolean togglePlayer(UUID playerId) {
        if (disabledPlayers.contains(playerId)) {
            disabledPlayers.remove(playerId);
            return true;
        } else {
            disabledPlayers.add(playerId);
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                removeEffect(player);
            }
            return false;
        }
    }

    public boolean isDisabled(UUID playerId) {
        return disabledPlayers.contains(playerId);
    }

    public int getAffectedCount() {
        return activeEffects.size();
    }

    public Set<UUID> getAffectedPlayers() {
        return new HashSet<>(activeEffects.keySet());
    }
}
