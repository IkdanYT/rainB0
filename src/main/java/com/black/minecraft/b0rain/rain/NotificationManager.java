package com.black.minecraft.b0rain.rain;

import com.black.minecraft.b0rain.config.ConfigManager;
import com.black.minecraft.b0rain.config.LanguageManager;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NotificationManager {
    private final ConfigManager configManager;
    private final LanguageManager languageManager;
    private final Map<UUID, Map<String, Long>> cooldowns;

    public NotificationManager(ConfigManager configManager, LanguageManager languageManager) {
        this.configManager = configManager;
        this.languageManager = languageManager;
        this.cooldowns = new HashMap<>();
    }

    public void notifyEffectStart(Player player, WeatherType weatherType) {
        if (!configManager.isNotifyOnEffectStart()) {
            return;
        }

        if (!canNotify(player, "effect-start")) {
            return;
        }

        String key = switch (weatherType) {
            case RAIN -> "effect-start-rain";
            case THUNDER -> "effect-start-thunder";
            case SNOW -> "effect-start-snow";
            default -> null;
        };

        if (key != null) {
            player.sendMessage(languageManager.getMessage(key));
            setCooldown(player, "effect-start");
        }
    }

    public void notifyEffectEnd(Player player) {
        if (!configManager.isNotifyOnEffectEnd()) {
            return;
        }

        if (!canNotify(player, "effect-end")) {
            return;
        }

        player.sendMessage(languageManager.getMessage("effect-end"));
        setCooldown(player, "effect-end");
    }

    public void notifyElytraBlocked(Player player) {
        if (!configManager.isNotifyOnElytraBlock()) {
            return;
        }

        if (!canNotify(player, "elytra-block")) {
            return;
        }

        player.sendMessage(languageManager.getMessage("elytra-blocked"));
        setCooldown(player, "elytra-block");
    }

    private boolean canNotify(Player player, String type) {
        UUID playerId = player.getUniqueId();
        Map<String, Long> playerCooldowns = cooldowns.get(playerId);

        if (playerCooldowns == null) {
            return true;
        }

        Long lastTime = playerCooldowns.get(type);
        if (lastTime == null) {
            return true;
        }

        long cooldownMs = configManager.getNotificationCooldown() * 1000L;
        return System.currentTimeMillis() - lastTime >= cooldownMs;
    }

    private void setCooldown(Player player, String type) {
        UUID playerId = player.getUniqueId();
        cooldowns.computeIfAbsent(playerId, k -> new HashMap<>())
                .put(type, System.currentTimeMillis());
    }

    public void clearPlayer(UUID playerId) {
        cooldowns.remove(playerId);
    }
}
