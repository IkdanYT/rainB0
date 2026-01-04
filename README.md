<p align="center">
  <img src="https://img.shields.io/github/v/release/IkdanYT/rainB0?style=for-the-badge&color=blue" alt="Release">
  <img src="https://img.shields.io/github/downloads/IkdanYT/rainB0/total?style=for-the-badge&color=green" alt="Downloads">
  <img src="https://img.shields.io/badge/Minecraft-1.18--1.21-orange?style=for-the-badge" alt="Minecraft">
  <img src="https://img.shields.io/badge/Java-17+-red?style=for-the-badge" alt="Java">
</p>

<h1 align="center">⛈️ WeatherFX</h1>

<p align="center">
  <b>Реалистичные эффекты погоды для Minecraft серверов</b><br>
  <i>Realistic weather effects for Minecraft servers</i>
</p>

<p align="center">
  <a href="#-features--возможности">Features</a> •
  <a href="#-installation--установка">Installation</a> •
  <a href="#-commands--команды">Commands</a> •
  <a href="#-permissions--права">Permissions</a> •
  <a href="#-configuration--конфигурация">Configuration</a>
</p>

---

## 📖 About | О плагине

**WeatherFX** — это переработанная и улучшенная версия плагина [RainB0](https://black-minecraft.com/resources/rainb0.10054/) от **b0b0b0**.

**WeatherFX** is a reworked and enhanced version of [RainB0](https://black-minecraft.com/resources/rainb0.10054/) plugin by **b0b0b0**.

### ✨ Что нового | What's New

- 🪂 Ограничения для элитр в плохую погоду
- 🔊 Атмосферные звуки дождя
- 💬 Уведомления с кулдауном
- ❄️ Отдельные эффекты для снега
- ⚡ Усиленные эффекты грозы
- 🔧 Полностью переработанная конфигурация

---

## ✨ Features | Возможности

| Feature | Description |
|---------|-------------|
| 🌧️ **Weather Effects** | Slowness, mining fatigue, darkness and more during rain/thunder/snow |
| 🪂 **Elytra Control** | Disable or slow down elytra flight in bad weather |
| 🔊 **Ambient Sounds** | Immersive rain sounds for players |
| 💬 **Notifications** | Customizable messages when effects are applied |
| 🌍 **World Control** | Whitelist/blacklist specific worlds |
| 🏔️ **Biome Control** | Whitelist/blacklist specific biomes |
| ❄️ **Snow Support** | Separate effects for snowy biomes |
| ⚡ **Thunder Effects** | Stronger effects during thunderstorms |
| 🔄 **Hot Reload** | Reload config without restart |
| 🌐 **Multi-language** | English and Russian support |

---

## 📥 Installation | Установка

### Requirements | Требования
- **Server:** Paper, Spigot, or Bukkit 1.18+
- **Java:** 17 or higher

### Steps | Шаги

1. Download the latest release from [Releases](https://github.com/IkdanYT/rainB0/releases)
2. Place `WeatherFX-X.X.jar` in your server's `plugins` folder
3. Restart the server
4. Configure in `plugins/WeatherFX/config.yml`

---

## 🎮 Commands | Команды

| Command | Description | Permission |
|---------|-------------|------------|
| `/wfx reload` | Reload configuration | `wfx.reload` |
| `/wfx status` | View plugin status | `wfx.status` |
| `/wfx toggle` | Toggle effect for yourself | `wfx.toggle` |
| `/wfx toggle <player>` | Toggle effect for another player | `wfx.toggle.others` |
| `/wfx info` | Plugin information | - |

**Aliases:** `/weatherfx`, `/rain`

---

## 🔐 Permissions | Права

| Permission | Description | Default |
|------------|-------------|---------|
| `wfx.reload` | Reload configuration | OP |
| `wfx.status` | View plugin status | OP |
| `wfx.toggle` | Toggle effect for yourself | Everyone |
| `wfx.toggle.others` | Toggle effect for other players | OP |
| `wfx.bypass` | Bypass all weather effects | Nobody |
| `wfx.elytra.bypass` | Bypass elytra restrictions | Nobody |

---

## ⚙️ Configuration | Конфигурация

<details>
<summary><b>📁 Full config.yml example (click to expand)</b></summary>

```yaml
# ╔═══════════════════════════════════════════════════════════════╗
# ║                    WeatherFX Configuration                    ║
# ╚═══════════════════════════════════════════════════════════════╝

# Language: en, ru
lang: en

# Check for updates on startup
check-update: true

# How often to check players (in ticks, 20 = 1 second)
check-interval-ticks: 40

# ── ELYTRA SETTINGS ──
elytra:
  enabled: true
  mode: disable          # "disable" or "slow"
  slow-multiplier: 0.3   # Speed for "slow" mode
  play-sound: true
  sound: ITEM_ELYTRA_FLYING

# ── NOTIFICATIONS ──
notifications:
  on-effect-start: true
  on-effect-end: false
  on-elytra-block: true
  cooldown-seconds: 30

# ── SOUNDS ──
sounds:
  enabled: true
  ambient:
    enabled: true
    sound: WEATHER_RAIN
    volume: 0.5
    pitch: 1.0
    interval-ticks: 100

# ── CONDITIONS ──
conditions:
  ignore-in-vehicle: true
  ignore-flying: true
  ignore-in-water: true
  ignored-gamemodes:
    - CREATIVE
    - SPECTATOR

# ── WORLDS ──
worlds:
  mode: blacklist        # "whitelist" or "blacklist"
  list:
    - "world_nether"
    - "world_the_end"

# ── BIOMES ──
biomes:
  mode: blacklist
  list:
    - "minecraft:desert"
    - "minecraft:savanna"
    - "minecraft:badlands"

# ── WEATHER EFFECTS ──
# Multiple effects per weather type!
effects:
  rain:
    enabled: true
    effects:
      - type: SLOW
        level: 0
        duration-ticks: 80

  thunder:
    enabled: true
    effects:
      - type: SLOW
        level: 1
        duration-ticks: 80

  snow:
    enabled: false
    effects:
      - type: SLOW
        level: 0
        duration-ticks: 60
```

</details>

### Key Configuration Options

#### 🪂 Elytra Modes

| Mode | Description |
|------|-------------|
| `disable` | Completely stops elytra flight, player falls |
| `slow` | Reduces flight speed by multiplier |

#### 🌍 World/Biome Modes

| Mode | Description |
|------|-------------|
| `whitelist` | Effects ONLY in listed worlds/biomes |
| `blacklist` | Effects EVERYWHERE except listed |

#### 🧪 Effect Types

Common effect types you can use:
- `SLOW` - Slowness
- `SLOW_DIGGING` - Mining Fatigue
- `BLINDNESS` - Blindness
- `HUNGER` - Hunger
- `WEAKNESS` - Weakness
- `DARKNESS` - Darkness (1.19+)

[Full list of effects](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffectType.html)

---

## 📝 Examples | Примеры

### Example 1: Hardcore Rain
```yaml
effects:
  rain:
    enabled: true
    effects:
      - type: SLOW
        level: 1
        duration-ticks: 100
      - type: SLOW_DIGGING
        level: 0
        duration-ticks: 100
```

### Example 2: Scary Thunder
```yaml
effects:
  thunder:
    enabled: true
    effects:
      - type: SLOW
        level: 2
        duration-ticks: 80
      - type: DARKNESS
        level: 0
        duration-ticks: 60
      - type: BLINDNESS
        level: 0
        duration-ticks: 40
```

### Example 3: Freezing Snow
```yaml
effects:
  snow:
    enabled: true
    effects:
      - type: SLOW
        level: 1
        duration-ticks: 80
      - type: HUNGER
        level: 0
        duration-ticks: 100
```

---

## 🔗 Links | Ссылки

### WeatherFX (This Fork)
- 📦 **Download:** [GitHub Releases](https://github.com/IkdanYT/rainB0/releases)
- 🐛 **Issues:** [GitHub Issues](https://github.com/IkdanYT/rainB0/issues)
- 👤 **Author:** [IkdanYT](https://github.com/IkdanYT)

### Original RainB0
- 🌐 **Website:** [black-minecraft.com](https://black-minecraft.com/resources/rainb0.10054/)
- 👤 **Author:** b0b0b0

---

## 👥 Credits | Авторы

| Role | Author |
|------|--------|
| **Original Plugin** | [b0b0b0](https://black-minecraft.com/members/b0b0b0.14914/) — RainB0 |
| **Fork & Improvements** | [IkdanYT](https://github.com/IkdanYT) — WeatherFX |

---

## 📜 License

This project is open source. Based on RainB0 by b0b0b0.

---

<p align="center">
  Made with ❤️ by <a href="https://github.com/IkdanYT">IkdanYT</a><br>
  <sub>Based on RainB0 by b0b0b0</sub>
</p>
