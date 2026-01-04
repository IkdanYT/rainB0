package com.black.minecraft.b0rain.config;

import com.black.minecraft.b0rain.B0rain;
import com.black.minecraft.b0rain.rain.PotionEffectHelper;
import org.bukkit.GameMode;
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

    public String getLanguage() {
        return config.getString("lang", "en");
    }

    public boolean isCheckUpdate() {
        return config.getBoolean("check-update", true);
    }

    public int getCheckIntervalTicks() {
        int value = config.getInt("check-interval-ticks", 40);
        return Math.max(1, value);
    }

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
        List<String> list = config.getStringList("conditions.ignored-gamemodes");
        for (String name : list) {
            try {
                modes.add(GameMode.valueOf(name.toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException ignored) {}
        }
        return modes;
    }

    private boolean isWhitelistMode(String path) {
        String mode = config.getString(path + ".mode", "blacklist").toLowerCase(Locale.ROOT);
        return mode.equals("whitelist");
    }

    private List<String> getList(String path) {
        return config.getStringList(path + ".list");
    }

    public boolean isWorldAllowed(String worldName) {
        boolean whitelist = isWhitelistMode("worlds");
        List<String> list = getList("worlds");
        boolean inList = list.contains(worldName);
        return whitelist ? inList : !inList;
    }

    public boolean isBiomeAllowed(String biomeName) {
        boolean whitelist = isWhitelistMode("biomes");
        List<String> list = getList("biomes");
        boolean inList = list.contains(biomeName);
        return whitelist ? inList : !inList;
    }

    public boolean isRainEnabled() {
        return config.getBoolean("effects.rain.enabled", true);
    }

    public boolean isThunderEnabled() {
        return config.getBoolean("effects.thunder.enabled", true);
    }

    public boolean isSnowEnabled() {
        return config.getBoolean("effects.snow.enabled", false);
    }

    public PotionEffect getRainEffect() {
        return getEffect("effects.rain");
    }

    public PotionEffect getThunderEffect() {
        return getEffect("effects.thunder");
    }

    public PotionEffect getSnowEffect() {
        return getEffect("effects.snow");
    }

    private PotionEffect getEffect(String path) {
        String type = config.getString(path + ".type", "SLOW");
        int level = config.getInt(path + ".level", 0);
        int duration = config.getInt(path + ".duration-ticks", 80);
        level = Math.max(0, Math.min(255, level));
        duration = Math.max(1, duration);
        return PotionEffectHelper.createEffect(type, duration, level);
    }

    public B0rain getPlugin() {
        return plugin;
    }
}
