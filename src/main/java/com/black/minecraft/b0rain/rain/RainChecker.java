package com.black.minecraft.b0rain.rain;

import com.black.minecraft.b0rain.config.ConfigManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Set;

public class RainChecker {
    private static final Set<String> SNOWY_BIOMES = Set.of(
            "minecraft:snowy_plains",
            "minecraft:snowy_taiga",
            "minecraft:snowy_beach",
            "minecraft:frozen_river",
            "minecraft:frozen_ocean",
            "minecraft:deep_frozen_ocean",
            "minecraft:grove",
            "minecraft:frozen_peaks",
            "minecraft:jagged_peaks",
            "minecraft:snowy_slopes",
            "minecraft:ice_spikes"
    );

    public static boolean shouldApplyEffect(Player player, ConfigManager config) {
        if (player.isDead()) return false;
        if (config.isIgnoreInVehicle() && player.isInsideVehicle()) return false;
        if (config.isIgnoreFlying() && player.isFlying()) return false;
        if (config.isIgnoreInWater() && player.isInWater()) return false;
        if (config.getIgnoredGamemodes().contains(player.getGameMode())) return false;
        if (!config.isWorldAllowed(player.getWorld().getName())) return false;

        String biome = player.getLocation().getBlock().getBiome().getKey().toString();
        if (!config.isBiomeAllowed(biome)) return false;

        return isUnderOpenSky(player);
    }

    public static WeatherType getWeatherType(Player player, ConfigManager config) {
        World world = player.getWorld();
        if (!world.hasStorm() && !world.isThundering()) {
            return WeatherType.NONE;
        }

        String biome = player.getLocation().getBlock().getBiome().getKey().toString();

        if (world.isThundering() && config.isThunderEnabled()) {
            return WeatherType.THUNDER;
        }

        if (world.hasStorm()) {
            if (isSnowyBiome(biome) && config.isSnowEnabled()) {
                return WeatherType.SNOW;
            }
            if (!isSnowyBiome(biome) && config.isRainEnabled()) {
                return WeatherType.RAIN;
            }
        }

        return WeatherType.NONE;
    }

    private static boolean isUnderOpenSky(Player player) {
        Location loc = player.getLocation();
        int highestY = player.getWorld().getHighestBlockYAt(loc.getBlockX(), loc.getBlockZ());
        return loc.getY() >= highestY;
    }

    public static boolean isSnowyBiome(String biomeName) {
        return SNOWY_BIOMES.contains(biomeName);
    }
}
