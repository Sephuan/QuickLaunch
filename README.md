# QuickLaunch

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-blue)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Compose-Material3-purple)](https://developer.android.com/jetpack/compose)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen)](https://android-developers.googleblog.com)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)
[![Release](https://img.shields.io/github/v/release/Sephuan/QuickLaunch)](https://github.com/Sephuan/QuickLaunch/releases/latest)

**QuickLaunch** is a highly customizable Android app launcher. Search, categorize, pin, and launch any installed app from a floating bubble anywhere — plus Quick Settings tiles, notification-bar search, and deep Material You theming.

**QuickLaunch** 是一款高度可定制的 Android 应用快速启动器。聚合搜索、分类管理、悬浮窗、快捷磁贴、通知栏搜索于一体，全面拥抱 Material You。

---

## 功能 / Features

| Feature | Description |
|---------|-------------|
| 🔍 **Smart Search** | Search by app name, package name, custom alias, or tags. Results ranked by relevance. |
| 🏷️ **Alias & Tags** | Give apps custom names and multiple tags. Search a tag to find all matching apps. |
| 📂 **Category Management** | Auto-categorize with built-in app database. Create, rename, drag-to-reorder, and manage app assignments. |
| 👻 **Floating Bubble** | Global overlay bubble — drag, auto-snap to edge, auto-hide. Tap to expand a search card. |
| 🧲 **Quick Settings Tiles** | Up to 12 customizable QS tiles. Assign any app + choose from 14 icons. Tap the tile to launch directly from notification shade. |
| 🔔 **Notification Search** | Expand the foreground notification, type a keyword, and launch the best-matching app — all from the notification shade. |
| 📊 **Usage Stats** | Launch count leaderboard with last-launch time. Long-press to edit alias/tags/pin. |
| ⭐ **Pinned Favorites** | Pin favorite apps to the home screen grid. |
| 🎨 **Theme System** | Monet (Material You) dynamic color or 8 custom color schemes (Blue, Green, Orange, Rose, Violet, Teal, Amber, Indigo). Auto-rotate mode. Light / Dark / System. |
| 🌐 **i18n** | 中文 / English. Full string resource coverage. |
| 🖥️ **Edge-to-Edge** | Immersive full-screen content behind status bar and navigation bar. |
| ↩️ **Predictive Back** | Android 13+ back-to-home preview animation. |
| ✨ **Transition Animations** | Slide-in/out and fade transitions between screens. |

---

## 截图 / Screenshots

*(screenshots are in the release assets)*

---

## 下载 / Download

[📦 Latest Release (APK)](https://github.com/Sephuan/QuickLaunch/releases/latest)

---

## 技术栈 / Tech Stack

- **Language**: Kotlin 2.0
- **UI**: Jetpack Compose + Material3 (full color scheme, typography)
- **Navigation**: Navigation Compose 2.8+ with animated transitions
- **Architecture**: Application-scoped shared instances (repository, config, category, settings managers)
- **Storage**: SharedPreferences + Gson (local key-value JSON)
- **System APIs**:
  - `WindowManager` + `LifecycleService` — global floating window
  - `TileService` — 12× Quick Settings tiles
  - `RemoteInput` / `Notification.Action` — notification-bar search
  - `PackageManager` — installed app enumeration
  - `AppCompatDelegate` — per-app language switching
- **Icons**: 14 custom vector drawables for tiles, Coil for lazy app icon loading

---

## 权限 / Permissions

| Permission | Purpose |
|------------|---------|
| `SYSTEM_ALERT_WINDOW` | Floating bubble overlay |
| `QUERY_ALL_PACKAGES` | Enumerate installed apps |
| `FOREGROUND_SERVICE` + `specialUse` | Keep floating service alive |

---

## 构建 / Build

```bash
git clone https://github.com/Sephuan/QuickLaunch.git
cd QuickLaunch
./gradlew assembleRelease
# APK at: app/build/outputs/apk/release/app-release.apk
```

Requires JDK 17+ and Android SDK 35.

---

## License

MIT © 2026 [Sephuan](https://github.com/Sephuan)
