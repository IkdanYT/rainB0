package com.black.minecraft.b0rain.config;

import com.black.minecraft.b0rain.B0rain;
import com.black.minecraft.b0rain.rain.PotionEffectHelper;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ConfigManager {
    private final B0rain plugin;
    private FileConfiguration config;

    public ConfigManager(B0rain plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    // ─────────────────────────────────────────────────────────────
    // GENERAL
    // ─────────────────────────────────────────────────────────────

    public String getLanguage() {
        return config.getString("lang", "en");
    }

    public boolean isCheckUpdate() {
        return config.getBoolean("check-update", true);
    }

    public int getCheckIntervalTicks() {
        return Math.max(1, config.getInt("check-interval-ticks", 40));
    }

    // ─────────────────────────────────────────────────────────────
    // ELYTRA
    // ─────────────────────────────────────────────────────────────

    public boolean isElytraEnabled() {
        return config.getBoolean("elytra.enabled", true);
    }

    public String getElytraMode() {
        return config.getString("elytra.mode", "disable").toLowerCase(Locale.ROOT);
    }

    public double getElytraSlowMultiplier() {
        double value = config.getDouble("elytra.slow-multiplier", 0.3);
        return Math.max(0.1, Math.min(1.0, value));
    }

    public boolean isElytraPlaySound() {
        return config.getBoolean("elytra.play-sound", true);
    }

    public Sound getElytraSound() {
        String soundName = config.getString("elytra.sound", "ITEM_ELYTRA_FLYING");
        try {
            return Sound.valueOf(soundName.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return Sound.ITEM_ELYTRA_FLYING;
        }
    }

    // ─────────────────────────────────────────────────────────────
    // NOTIFICATIONS
    // ─────────────────────────────────────────────────────────────

    public boolean isNotifyOnEffectStart() {
        return config.getBoolean("notifications.on-effect-start", true);
    }

    public boolean isNotifyOnEffectEnd() {
        return config.getBoolean("notifications.on-effect-end", false);
    }

    public boolean isNotifyOnElytraBlock() {
        return config.getBoolean("notifications.on-elytra-block", true);
    }

    public int getNotificationCooldown() {
        return Math.max(0, config.getInt("notifications.cooldown-seconds", 30));
    }

    // ─────────────────────────────────────────────────────────────
    // SOUNDS
    // ─────────────────────────────────────────────────────────────

    public boolean isSoundsEnabled() {
        return config.getBoolean("sounds.enabled", true);
    }

    public boolean isAmbientSoundEnabled() {
        return config.getBoolean("sounds.ambient.enabled", true);
    }

    public Sound getAmbientSound() {
        String soundName = config.getString("sounds.ambient.sound", "WEATHER_RAIN");
        try {
            return Sound.valueOf(soundName.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return Sound.WEATHER_RAIN;
        }
    }

    public float getAmbientVolume() {
        return (float) Math.max(0.0, Math.min(1.0, config.getDouble("sounds.ambient.volume", 0.5)));
    }

    public float getAmbientPitch() {
        return (float) Math.max(0.5, Math.min(2.0, config.getDouble("sounds.ambient.pitch", 1.0)));
    }

    public int getAmbientInterval() {
        return Math.max(20, config.getInt("sounds.ambient.interval-ticks", 100));
    }

    // ─────────────────────────────────────────────────────────────
    // CONDITIONS
    // ─────────────────────────────────────────────────────────────

    public boolean isIgnoreInVehicle() {
        return config.getBoolean("conditions.ignore-in-vehicle", true);
    }

    public boolean isIgnoreFlying() {
        return config.getBoolean("conditions.ignore-flying", true);
    }

    public boolean isIgnoreInWater() {
        return config.getBoolean("conditions.ignore-in-water", true);
    }

    public List<GameMode> getIgnoredGamemodes() {
        List<GameMode> modes = new ArrayList<>();
        for (String name : config.getStringList("conditions.ignored-gamemodes")) {
            try {
                modes.add(GameMode.valueOf(name.toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException ignored) {}
        }
        return modes;
    }

    // ─────────────────────────────────────────────────────────────
    // WORLDS & BIOMES
    // ─────────────────────────────────────────────────────────────

    public boolean isWorldAllowed(String worldName) {
        boolean whitelist = config.getString("worlds.mode", "blacklist")
                .equalsIgnoreCase("whitelist");
        List<String> list = config.getStringList("worlds.list");
        return whitelist ? list.contains(worldName) : !list.contains(worldName);
    }

    public boolean isBiomeAllowed(String biomeName) {
        boolean whitelist = config.getString("biomes.mode", "blacklist")
                .equalsIgnoreCase("whitelist");
        List<String> list = config.getStringList("biomes.list");
        return whitelist ? list.contains(biomeName) : !list.contains(biomeName);
    }

    // ─────────────────────────────────────────────────────────────
    // EFFECTS
    // ─────────────────────────────────────────────────────────────

    public boolean isRainEnabled() {
        return config.getBoolean("effects.rain.enabled", true);
    }

    public boolean isThunderEnabled() {
        return config.getBoolean("effects.thunder.enabled", true);
    }

    public boolean isSnowEnabled() {
        return config.getBoolean("effects.snow.enabled", false);
    }

    public List<PotionEffect> getRainEffects() {
        return getEffectsList("effects.rain.effects");
    }

    public List<PotionEffect> getThunderEffects() {
        return getEffectsList("effects.thunder.effects");
    }

    public List<PotionEffect> getSnowEffects() {
        return getEffectsList("effects.snow.effects");
    }

    private List<PotionEffect> getEffectsList(String path) {
        List<PotionEffect> effects = new ArrayList<>();
        if (!config.contains(path)) {
            return effects;
        }

        List<?> effectsList = config.getList(path);
        if (effectsList == null) {
            return effects;
        }

        for (Object obj : effectsList) {
            if (obj instanceof ConfigurationSection section) {
                PotionEffect effect = parseEffect(section);
                if (effect != null) {
                    effects.add(effect);
                }
            } else if (obj instanceof java.util.Map) {
                @SuppressWarnings("unchecked")
                java.util.Map<String, Object> map = (java.util.Map<String, Object>) obj;
                String type = (String) map.getOrDefault("type", "SLOW");
                int level = ((Number) map.getOrDefault("level", 0)).intValue();
                int duration = ((Number) map.getOrDefault("duration-ticks", 80)).intValue();
                PotionEffect effect = PotionEffectHelper.createEffect(type, duration, level);
                if (effect != null) {
                    effects.add(effect);
                }
            }
        }

        return effects;
    }

    private PotionEffect parseEffect(ConfigurationSection section) {
        String type = section.getString("type", "SLOW");
        int level = section.getInt("level", 0);
        int duration = section.getInt("duration-ticks", 80);
        return PotionEffectHelper.createEffect(type, duration, level);
    }

    public B0rain getPlugin() {
        return plugin;
    }
}
