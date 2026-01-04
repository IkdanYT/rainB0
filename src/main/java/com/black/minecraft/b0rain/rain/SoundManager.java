package com.black.minecraft.b0rain.rain;

import com.black.minecraft.b0rain.B0rain;
import com.black.minecraft.b0rain.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SoundManager {
    private final B0rain plugin;
    private final ConfigManager configManager;
    private final Set<UUID> playersWithAmbient;
    private BukkitRunnable ambientTask;

    public SoundManager(B0rain plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.playersWithAmbient = new HashSet<>();
    }

    public void start() {
        if (ambientTask != null) {
            ambientTask.cancel();
        }

        if (!configManager.isSoundsEnabled() || !configManager.isAmbientSoundEnabled()) {
            return;
        }

        ambientTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID playerId : new HashSet<>(playersWithAmbient)) {
                    Player player = Bukkit.getPlayer(playerId);
                    if (player != null && player.isOnline()) {
                        playAmbientSound(player);
                    }
                }
            }
        };

        ambientTask.runTaskTimer(plugin, 0L, configManager.getAmbientInterval());
    }

    public void stop() {
        if (ambientTask != null) {
            ambientTask.cancel();
            ambientTask = null;
        }
        playersWithAmbient.clear();
    }

    public void addPlayerAmbient(UUID playerId) {
        playersWithAmbient.add(playerId);
    }

    public void removePlayerAmbient(UUID playerId) {
        playersWithAmbient.remove(playerId);
    }

    public void playAmbientSound(Player player) {
        if (!configManager.isSoundsEnabled() || !configManager.isAmbientSoundEnabled()) {
            return;
        }

        player.playSound(
                player.getLocation(),
                configManager.getAmbientSound(),
                configManager.getAmbientVolume(),
                configManager.getAmbientPitch()
        );
    }

    public void playElytraBlockSound(Player player) {
        if (!configManager.isElytraPlaySound()) {
            return;
        }

        player.playSound(
                player.getLocation(),
                configManager.getElytraSound(),
                1.0f,
                0.5f
        );
    }
}
