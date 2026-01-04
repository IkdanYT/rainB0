package com.black.minecraft.b0rain.utils;

import com.black.minecraft.b0rain.B0rain;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class VersionChecker {
    private static final String VERSION_URL = "https://b0b0b0.dev/pl/b0rain.txt";
    private static final ConsoleCommandSender console = Bukkit.getConsoleSender();

    public static void checkVersion(B0rain plugin, String currentVersion) {
        String latestVersion = fetchLatestVersion(currentVersion);
        if (latestVersion == null) {
            logError("Failed to fetch the latest version.");
            return;
        }

        Bukkit.getScheduler().runTask(plugin, () -> {
            if (!currentVersion.equalsIgnoreCase(latestVersion)) {
                console.sendMessage(B0rain.PREFIX + " ");
                logWarning("You are using an outdated version!");
                logWarning("Current: \u001B[90m" + currentVersion + 
                        "\u001B[33m, Latest: \u001B[32m" + latestVersion + "\u001B[0m");
                logWarning("Download: \u001B[36mhttps://bm.wtf/resources/10054/\u001B[0m");
                console.sendMessage(B0rain.PREFIX + " ");
            } else {
                logInfo("Running latest version: \u001B[32m" + currentVersion + "\u001B[0m");
            }
        });
    }

    private static String fetchLatestVersion(String currentVersion) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(VERSION_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestProperty("User-Agent", "RainB0/" + currentVersion);

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()))) {
                String line = reader.readLine();
                return line != null ? line.trim() : null;
            }
        } catch (Exception e) {
            logError("Connection error: " + e.getMessage());
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static void logInfo(String message) {
        console.sendMessage(B0rain.PREFIX + "\u001B[32m" + message + "\u001B[0m");
    }

    private static void logWarning(String message) {
        console.sendMessage(B0rain.PREFIX + "\u001B[33m" + message + "\u001B[0m");
    }

    private static void logError(String message) {
        console.sendMessage(B0rain.PREFIX + "\u001B[31m" + message + "\u001B[0m");
    }
}
