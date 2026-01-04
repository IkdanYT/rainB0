package com.black.minecraft.b0rain.rain;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import java.util.List;
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

    public static boolean isInRain(Player player, boolean checkThunder, boolean checkSnow, List<String> ignoredBiomes) {
        if (player.isInsideVehicle() || player.isDead() || player.isInWater()) {
            return false;
        }

        World world = player.getWorld();
        if (!world.hasStorm() && !(checkThunder && world.isThundering())) {
            return false;
        }

        Location location = player.getLocation();
        String biomeName = location.getBlock().getBiome().getKey().toString();

        if (ignoredBiomes != null && ignoredBiomes.contains(biomeName)) {
            return false;
        }

        boolean isSnowyBiome = SNOWY_BIOMES.contains(biomeName);
        if (isSnowyBiome && !checkSnow) {
            return false;
        }

        int blockX = location.getBlockX();
        int blockZ = location.getBlockZ();
        int highestY = world.getHighestBlockYAt(blockX, blockZ);

        return location.getY() >= highestY;
    }

    public static boolean isSnowyBiome(String biomeName) {
        return SNOWY_BIOMES.contains(biomeName);
    }
}
