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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class RainEffectManager implements Listener {
    private final B0rain plugin;
    private final ConfigManager configManager;
    private final NotificationManager notificationManager;
    private final SoundManager soundManager;
    private BukkitRunnable task;
    private final Set<UUID> disabledPlayers;
    private final Map<UUID, Set<PotionEffectType>> activeEffects;
    private final Map<UUID, WeatherType> playerWeatherState;

    public RainEffectManager(B0rain plugin, ConfigManager configManager,
                             NotificationManager notificationManager, SoundManager soundManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.notificationManager = notificationManager;
        this.soundManager = soundManager;
        this.disabledPlayers = new HashSet<>();
        this.activeEffects = new HashMap<>();
        this.playerWeatherState = new HashMap<>();
    }

    public void start() {
        if (task != null) {
            task.cancel();
        }

        Bukkit.getPluginManager().registerEvents(this, plugin);
        soundManager.start();

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

        if (player.hasPermission("wfx.bypass") || disabledPlayers.contains(playerId)) {
            removeEffects(player);
            return;
        }

        if (!RainChecker.shouldApplyEffect(player, configManager)) {
            removeEffects(player);
            return;
        }

        WeatherType weather = RainChecker.getWeatherType(player, configManager);
        List<PotionEffect> effects = getEffectsForWeather(weather);

        if (effects.isEmpty()) {
            removeEffects(player);
            return;
        }

        WeatherType previousWeather = playerWeatherState.get(playerId);
        boolean isNewEffect = previousWeather != weather;

        for (PotionEffect effect : effects) {
            player.addPotionEffect(effect);
            activeEffects.computeIfAbsent(playerId, k -> new HashSet<>())
                    .add(effect.getType());
        }

        if (isNewEffect && weather != WeatherType.NONE) {
            notificationManager.notifyEffectStart(player, weather);
            playerWeatherState.put(playerId, weather);
        }

        soundManager.addPlayerAmbient(playerId);
    }

    private List<PotionEffect> getEffectsForWeather(WeatherType weather) {
        return switch (weather) {
            case RAIN -> configManager.getRainEffects();
            case THUNDER -> configManager.getThunderEffects();
            case SNOW -> configManager.getSnowEffects();
            default -> List.of();
        };
    }

    private void removeEffects(Player player) {
        UUID playerId = player.getUniqueId();
        Set<PotionEffectType> types = activeEffects.remove(playerId);

        if (types != null && !types.isEmpty()) {
            for (PotionEffectType type : types) {
                player.removePotionEffect(type);
            }

            WeatherType previous = playerWeatherState.remove(playerId);
            if (previous != null && previous != WeatherType.NONE) {
                notificationManager.notifyEffectEnd(player);
            }
        }

        soundManager.removePlayerAmbient(playerId);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }

        soundManager.stop();

        for (UUID playerId : new HashSet<>(activeEffects.keySet())) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                removeEffects(player);
            }
        }
        activeEffects.clear();
        playerWeatherState.clear();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        activeEffects.remove(playerId);
        disabledPlayers.remove(playerId);
        playerWeatherState.remove(playerId);
        soundManager.removePlayerAmbient(playerId);
        notificationManager.clearPlayer(playerId);
    }

    public boolean togglePlayer(UUID playerId) {
        if (disabledPlayers.contains(playerId)) {
            disabledPlayers.remove(playerId);
            return true;
        } else {
            disabledPlayers.add(playerId);
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                removeEffects(player);
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
