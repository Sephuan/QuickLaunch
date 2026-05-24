package com.sephuan.quicklaunch.data

import android.content.Context
import android.content.res.Configuration
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.compose.runtime.mutableStateOf
import com.sephuan.quicklaunch.App

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

enum class ColorSource {
    MONET, CUSTOM
}

enum class CustomColorScheme(val displayNameKey: String) {
    BLUE("blue"),
    GREEN("green"),
    ORANGE("orange"),
    ROSE("rose"),
    VIOLET("violet"),
    TEAL("teal"),
    AMBER("amber"),
    INDIGO("indigo")
}

data class QuickTileSlot(
    val slotId: Int,
    val packageName: String = "",
    val label: String = "",
    val iconResId: Int = 0
)

class SettingsManager(private val context: Context) {
    private val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    private val gson = Gson()

    var themeMode: ThemeMode
        get() = when (prefs.getString(KEY_THEME_MODE, "SYSTEM")) {
            "LIGHT" -> ThemeMode.LIGHT
            "DARK" -> ThemeMode.DARK
            else -> ThemeMode.SYSTEM
        }
        set(value) {
            prefs.edit().putString(KEY_THEME_MODE, value.name).apply()
            notifyThemeChanged()
        }

    var colorSource: ColorSource
        get() = if (prefs.getString(KEY_COLOR_SOURCE, "MONET") == "CUSTOM") ColorSource.CUSTOM else ColorSource.MONET
        set(value) {
            prefs.edit().putString(KEY_COLOR_SOURCE, value.name).apply()
            notifyThemeChanged()
        }

    var customColorScheme: CustomColorScheme
        get() = when (prefs.getString(KEY_CUSTOM_COLOR, "BLUE")) {
            "GREEN" -> CustomColorScheme.GREEN
            "ORANGE" -> CustomColorScheme.ORANGE
            "ROSE" -> CustomColorScheme.ROSE
            "VIOLET" -> CustomColorScheme.VIOLET
            "TEAL" -> CustomColorScheme.TEAL
            "AMBER" -> CustomColorScheme.AMBER
            "INDIGO" -> CustomColorScheme.INDIGO
            else -> CustomColorScheme.BLUE
        }
        set(value) {
            prefs.edit().putString(KEY_CUSTOM_COLOR, value.name).apply()
            notifyThemeChanged()
        }

    var language: String
        get() = prefs.getString(KEY_LANGUAGE, "auto") ?: "auto"
        set(value) {
            prefs.edit().putString(KEY_LANGUAGE, value).apply()
            applyLanguage(value)
        }

    var immersiveMode: Boolean
        get() = prefs.getBoolean(KEY_IMMERSIVE, true)
        set(value) = prefs.edit().putBoolean(KEY_IMMERSIVE, value).apply()

    var autoRotateColors: Boolean
        get() = prefs.getBoolean(KEY_AUTO_ROTATE, false)
        set(value) = prefs.edit().putBoolean(KEY_AUTO_ROTATE, value).apply()

    fun getTileSlot(slotId: Int): QuickTileSlot? {
        val json = prefs.getString(KEY_TILE_SLOTS, null) ?: return null
        val type = object : TypeToken<List<QuickTileSlot>>() {}.type
        val slots: List<QuickTileSlot> = try { gson.fromJson(json, type) } catch (_: Exception) { emptyList() }
        return slots.find { it.slotId == slotId }
    }

    fun saveTileSlot(slot: QuickTileSlot) {
        val json = prefs.getString(KEY_TILE_SLOTS, null)
        val type = object : TypeToken<MutableList<QuickTileSlot>>() {}.type
        val slots: MutableList<QuickTileSlot> = try {
            gson.fromJson(json, type) ?: mutableListOf()
        } catch (_: Exception) { mutableListOf() }
        val idx = slots.indexOfFirst { it.slotId == slot.slotId }
        if (idx >= 0) slots[idx] = slot else slots.add(slot)
        prefs.edit().putString(KEY_TILE_SLOTS, gson.toJson(slots)).apply()
    }

    fun removeTileSlot(slotId: Int) {
        val json = prefs.getString(KEY_TILE_SLOTS, null) ?: return
        val type = object : TypeToken<MutableList<QuickTileSlot>>() {}.type
        val slots: MutableList<QuickTileSlot> = try {
            gson.fromJson(json, type) ?: mutableListOf()
        } catch (_: Exception) { mutableListOf() }
        slots.removeAll { it.slotId == slotId }
        prefs.edit().putString(KEY_TILE_SLOTS, gson.toJson(slots)).apply()
    }

    fun isDarkTheme(): Boolean = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> {
            val nightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            nightMode == Configuration.UI_MODE_NIGHT_YES
        }
    }

    private fun notifyThemeChanged() {
        (context.applicationContext as? App)?.syncThemeState()
    }

    private fun applyLanguage(lang: String) {
        val localeList = when (lang) {
            "zh" -> androidx.core.os.LocaleListCompat.forLanguageTags("zh-CN")
            "en" -> androidx.core.os.LocaleListCompat.forLanguageTags("en-US")
            else -> androidx.core.os.LocaleListCompat.getEmptyLocaleList()
        }
        androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(localeList)
    }

    companion object {
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_COLOR_SOURCE = "color_source"
        private const val KEY_CUSTOM_COLOR = "custom_color"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_IMMERSIVE = "immersive_mode"
        private const val KEY_AUTO_ROTATE = "auto_rotate"
        private const val KEY_TILE_SLOTS = "tile_slots"
    }
}
