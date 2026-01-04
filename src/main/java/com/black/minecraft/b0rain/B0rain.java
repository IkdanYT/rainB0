package com.black.minecraft.b0rain;

import com.black.minecraft.b0rain.commands.B0rainCommand;
import com.black.minecraft.b0rain.config.ConfigManager;
import com.black.minecraft.b0rain.config.LanguageManager;
import com.black.minecraft.b0rain.rain.RainEffectManager;
import com.black.minecraft.b0rain.utils.VersionChecker;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class B0rain extends JavaPlugin {
    public static final String PREFIX = "\u001B[37m[\u001B[90mRainB0\u001B[37m]\u001B[0m ";
    
    private ConfigManager configManager;
    private LanguageManager languageManager;
    private RainEffectManager rainEffectManager;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        languageManager = new LanguageManager(this, configManager);
        rainEffectManager = new RainEffectManager(this, configManager);

        B0rainCommand commandHandler = new B0rainCommand(this, configManager, languageManager);
        getCommand("rainb0").setExecutor(commandHandler);
        getCommand("rainb0").setTabCompleter(commandHandler);

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
    }

    public RainEffectManager getRainEffectManager() {
        return rainEffectManager;
    }
}
