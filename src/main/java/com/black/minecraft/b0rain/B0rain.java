package com.black.minecraft.b0rain;

import com.black.minecraft.b0rain.commands.B0rainCommand;
import com.black.minecraft.b0rain.config.ConfigManager;
import com.black.minecraft.b0rain.config.LanguageManager;
import com.black.minecraft.b0rain.rain.ElytraHandler;
import com.black.minecraft.b0rain.rain.NotificationManager;
import com.black.minecraft.b0rain.rain.RainEffectManager;
import com.black.minecraft.b0rain.rain.SoundManager;
import com.black.minecraft.b0rain.utils.VersionChecker;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class B0rain extends JavaPlugin {
    public static final String PREFIX = "\u001B[37m[\u001B[96mWeatherFX\u001B[37m]\u001B[0m ";

    private ConfigManager configManager;
    private LanguageManager languageManager;
    private NotificationManager notificationManager;
    private SoundManager soundManager;
    private RainEffectManager rainEffectManager;
    private ElytraHandler elytraHandler;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        languageManager = new LanguageManager(this, configManager);
        notificationManager = new NotificationManager(configManager, languageManager);
        soundManager = new SoundManager(this, configManager);
        rainEffectManager = new RainEffectManager(this, configManager, notificationManager, soundManager);
        elytraHandler = new ElytraHandler(this, configManager, notificationManager, soundManager);

        B0rainCommand commandHandler = new B0rainCommand(this, configManager, languageManager);
        getCommand("wfx").setExecutor(commandHandler);
        getCommand("wfx").setTabCompleter(commandHandler);

        Bukkit.getPluginManager().registerEvents(elytraHandler, this);
        rainEffectManager.start();

        if (configManager.isCheckUpdate()) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(this, () ->
                    VersionChecker.checkVersion(this, getDescription().getVersion()), 60L);
        }
    }

    @Override
    public void onDisable() {
        if (rainEffectManager != null) {
            rainEffectManager.stop();
        }
        if (soundManager != null) {
            soundManager.stop();
        }
    }

    public RainEffectManager getRainEffectManager() {
        return rainEffectManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }
}
