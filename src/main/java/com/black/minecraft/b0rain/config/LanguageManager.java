package com.black.minecraft.b0rain.config;

import com.black.minecraft.b0rain.B0rain;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class LanguageManager {
    private static final String[] SUPPORTED_LANGUAGES = {"ru", "en"};
    
    private final B0rain plugin;
    private final ConfigManager configManager;
    private final File langFolder;
    private FileConfiguration languageConfig;
    private String currentLanguage;

    public LanguageManager(B0rain plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.langFolder = new File(plugin.getDataFolder(), "lang");
        initializeLanguageFiles();
        loadLanguage();
    }

    private void initializeLanguageFiles() {
        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }
        for (String lang : SUPPORTED_LANGUAGES) {
            copyResourceIfMissing("lang_" + lang + ".yml");
        }
    }

    private void copyResourceIfMissing(String fileName) {
        File targetFile = new File(langFolder, fileName);
        if (targetFile.exists()) {
            return;
        }
        try (InputStream in = plugin.getResource(fileName)) {
            if (in == null) return;
            try (FileOutputStream out = new FileOutputStream(targetFile)) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to copy: " + fileName);
        }
    }

    public void loadLanguage() {
        String lang = configManager.getLanguage().toLowerCase(Locale.ROOT);
        this.currentLanguage = (lang.equals("ru") || lang.equals("en")) ? lang : "en";

        String fileName = "lang_" + currentLanguage + ".yml";
        copyResourceIfMissing(fileName);

        File langFile = new File(langFolder, fileName);
        languageConfig = YamlConfiguration.loadConfiguration(langFile);

        try (InputStream defaultStream = plugin.getResource(fileName)) {
            if (defaultStream != null) {
                YamlConfiguration defaults = YamlConfiguration.loadConfiguration(
                        new InputStreamReader(defaultStream, StandardCharsets.UTF_8));
                languageConfig.setDefaults(defaults);
            }
        } catch (Exception ignored) {}
    }

    public String getMessage(String key) {
        return languageConfig.getString("messages." + key, key);
    }

    public String getCurrentLanguage() {
        return currentLanguage;
    }
}
