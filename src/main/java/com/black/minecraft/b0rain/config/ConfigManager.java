package com.black.minecraft.b0rain.config;

import com.black.minecraft.b0rain.B0rain;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

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

    public int getCheckIntervalTicks() {
        int value = config.getInt("check-interval-ticks", 40);
        return Math.max(1, value);
    }

    public int getSlownessLevel() {
        int value = config.getInt("slowness-level", 0);
        return Math.max(0, Math.min(255, value));
    }

    public int getSlownessDurationTicks() {
        int value = config.getInt("slowness-duration-ticks", 80);
        return Math.max(1, value);
    }

    public boolean isCheckThunder() {
        return config.getBoolean("check-thunder", true);
    }

    public boolean isCheckSnow() {
        return config.getBoolean("check-snow", false);
    }

    public List<String> getEnabledWorlds() {
        return config.getStringList("enabled-worlds");
    }

    public boolean isWorldEnabled(String worldName) {
        List<String> enabledWorlds = getEnabledWorlds();
        return enabledWorlds.isEmpty() || enabledWorlds.contains(worldName);
    }

    public List<String> getIgnoredBiomes() {
        return config.getStringList("ignored-biomes");
    }

    public String getLanguage() {
        return config.getString("lang", "en");
    }

    public boolean isCheckUpdate() {
        return config.getBoolean("check-update", true);
    }

    public B0rain getPlugin() {
        return plugin;
    }
}
