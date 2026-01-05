package com.black.minecraft.b0rain.utils;

import com.black.minecraft.b0rain.B0rain;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionChecker {
    private static final String GITHUB_API_URL = "https://api.github.com/repos/IkdanYT/rainB0/releases/latest";
    private static final String DOWNLOAD_URL = "https://github.com/IkdanYT/rainB0/releases";
    private static final ConsoleCommandSender console = Bukkit.getConsoleSender();

    public static void checkVersion(B0rain plugin, String currentVersion) {
        String latestVersion = fetchLatestVersion(currentVersion);
        if (latestVersion == null) {
            return;
        }

        Bukkit.getScheduler().runTask(plugin, () -> {
            if (!currentVersion.equalsIgnoreCase(latestVersion)) {
                console.sendMessage(B0rain.PREFIX + " ");
                logWarning("You are using an outdated version!");
                logWarning("Current: \u001B[90m" + currentVersion + 
                        "\u001B[33m, Latest: \u001B[32m" + latestVersion + "\u001B[0m");
                logWarning("Download: \u001B[36m" + DOWNLOAD_URL + "\u001B[0m");
                console.sendMessage(B0rain.PREFIX + " ");
            } else {
                logInfo("Running latest version: \u001B[32m" + currentVersion + "\u001B[0m");
            }
        });
    }

    private static String fetchLatestVersion(String currentVersion) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(GITHUB_API_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestProperty("User-Agent", "WeatherFX/" + currentVersion);
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                return null;
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            return parseTagName(response.toString());
        } catch (Exception e) {
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static String parseTagName(String json) {
        Pattern pattern = Pattern.compile("\"tag_name\"\\s*:\\s*\"v?([^\"]+)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private static void logInfo(String message) {
        console.sendMessage(B0rain.PREFIX + "\u001B[32m" + message + "\u001B[0m");
    }

    private static void logWarning(String message) {
        console.sendMessage(B0rain.PREFIX + "\u001B[33m" + message + "\u001B[0m");
    }
}
