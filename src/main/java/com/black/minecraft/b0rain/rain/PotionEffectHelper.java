package com.black.minecraft.b0rain.rain;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectHelper {
    private static final PotionEffectType SLOWNESS = PotionEffectType.SLOW;

    public static PotionEffectType getSlownessType() {
        return SLOWNESS;
    }

    public static PotionEffect createSlownessEffect(int duration, int level) {
        return new PotionEffect(SLOWNESS, duration, level, false, false);
    }
}
