package com.black.minecraft.b0rain.commands;

import com.black.minecraft.b0rain.B0rain;
import com.black.minecraft.b0rain.config.ConfigManager;
import com.black.minecraft.b0rain.config.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class B0rainCommand implements CommandExecutor, TabCompleter {
    private final B0rain plugin;
    private final ConfigManager configManager;
    private final LanguageManager languageManager;
    private static final List<String> COMMANDS = Arrays.asList("reload", "status", "toggle", "info");

    public B0rainCommand(B0rain plugin, ConfigManager configManager, LanguageManager languageManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.languageManager = languageManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "reload":
                return handleReload(sender);
            case "status":
                return handleStatus(sender);
            case "toggle":
                return handleToggle(sender, args);
            case "info":
                return handleInfo(sender);
            default:
                sendHelp(sender);
                return true;
        }
    }

    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("wfx.reload")) {
            sender.sendMessage(languageManager.getMessage("no-permission"));
            return true;
        }

        try {
            configManager.loadConfig();
            languageManager.loadLanguage();
            plugin.getSoundManager().stop();
            plugin.getRainEffectManager().stop();
            plugin.getRainEffectManager().start();
            sender.sendMessage(languageManager.getMessage("reload-success"));
        } catch (Exception e) {
            sender.sendMessage(languageManager.getMessage("reload-error"));
            plugin.getLogger().severe("Error reloading configuration: " + e.getMessage());
        }
        return true;
    }

    private boolean handleStatus(CommandSender sender) {
        if (!sender.hasPermission("wfx.status")) {
            sender.sendMessage(languageManager.getMessage("no-permission"));
            return true;
        }

        int affectedCount = plugin.getRainEffectManager().getAffectedCount();
        int onlineCount = Bukkit.getOnlinePlayers().size();

        sender.sendMessage(languageManager.getMessage("status-header"));
        sender.sendMessage(languageManager.getMessage("status-enabled")
                .replace("%status%", "§a✓"));
        sender.sendMessage(languageManager.getMessage("status-players")
                .replace("%affected%", String.valueOf(affectedCount))
                .replace("%online%", String.valueOf(onlineCount)));
        sender.sendMessage(languageManager.getMessage("status-worlds")
                .replace("%worlds%", "configured"));
        return true;
    }

    private boolean handleToggle(CommandSender sender, String[] args) {
        Player target;

        if (args.length >= 2) {
            if (!sender.hasPermission("wfx.toggle.others")) {
                sender.sendMessage(languageManager.getMessage("no-permission"));
                return true;
            }
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(languageManager.getMessage("player-not-found")
                        .replace("%player%", args[1]));
                return true;
            }
        } else {
            if (!sender.hasPermission("wfx.toggle")) {
                sender.sendMessage(languageManager.getMessage("no-permission"));
                return true;
            }
            if (!(sender instanceof Player)) {
                sender.sendMessage(languageManager.getMessage("player-only"));
                return true;
            }
            target = (Player) sender;
        }

        boolean enabled = plugin.getRainEffectManager().togglePlayer(target.getUniqueId());

        if (target.equals(sender)) {
            if (enabled) {
                sender.sendMessage(languageManager.getMessage("toggle-enabled"));
            } else {
                sender.sendMessage(languageManager.getMessage("toggle-disabled"));
            }
        } else {
            if (enabled) {
                sender.sendMessage(languageManager.getMessage("toggle-player-enabled")
                        .replace("%player%", target.getName()));
            } else {
                sender.sendMessage(languageManager.getMessage("toggle-player-disabled")
                        .replace("%player%", target.getName()));
            }
        }
        return true;
    }

    private boolean handleInfo(CommandSender sender) {
        sender.sendMessage(languageManager.getMessage("info-header"));
        sender.sendMessage(languageManager.getMessage("info-version")
                .replace("%version%", plugin.getDescription().getVersion()));
        sender.sendMessage(languageManager.getMessage("info-author")
                .replace("%author%", String.join(", ", plugin.getDescription().getAuthors())));
        sender.sendMessage(languageManager.getMessage("info-website")
                .replace("%website%", plugin.getDescription().getWebsite() != null 
                        ? plugin.getDescription().getWebsite() : "N/A"));
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(languageManager.getMessage("help-header"));
        sender.sendMessage("§7/wfx reload §8- §f" + languageManager.getMessage("help-reload"));
        sender.sendMessage("§7/wfx status §8- §f" + languageManager.getMessage("help-status"));
        sender.sendMessage("§7/wfx toggle [player] §8- §f" + languageManager.getMessage("help-toggle"));
        sender.sendMessage("§7/wfx info §8- §f" + languageManager.getMessage("help-info"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String input = args[0].toLowerCase();
            for (String cmd : COMMANDS) {
                if (cmd.startsWith(input)) {
                    if (cmd.equals("reload") && !sender.hasPermission("wfx.reload")) continue;
                    if (cmd.equals("status") && !sender.hasPermission("wfx.status")) continue;
                    if (cmd.equals("toggle") && !sender.hasPermission("wfx.toggle") 
                            && !sender.hasPermission("wfx.toggle.others")) continue;
                    completions.add(cmd);
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("toggle")) {
            if (sender.hasPermission("wfx.toggle.others")) {
                String input = args[1].toLowerCase();
                completions = Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> name.toLowerCase().startsWith(input))
                        .collect(Collectors.toList());
            }
        }

        return completions;
    }
}
