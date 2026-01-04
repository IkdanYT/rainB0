package com.black.minecraft.b0rain.rain;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectHelper {

    public static PotionEffect createEffect(String typeName, int duration, int level) {
        PotionEffectType type = PotionEffectType.getByName(typeName);
        if (type == null) {
            type = PotionEffectType.SLOW;
        }
        return new PotionEffect(type, duration, level, false, false);
    }

    public static PotionEffectType getEffectType(PotionEffect effect) {
        return effect != null ? effect.getType() : null;
    }
}
