package com.sephuan.quicklaunch

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import com.sephuan.quicklaunch.data.AppConfigManager
import com.sephuan.quicklaunch.data.AppRepository
import com.sephuan.quicklaunch.data.CategoryManager
import com.sephuan.quicklaunch.data.ColorSource
import com.sephuan.quicklaunch.data.CustomColorScheme
import com.sephuan.quicklaunch.data.SettingsManager

class App : Application() {
    lateinit var repository: AppRepository
    lateinit var configManager: AppConfigManager
    lateinit var categoryManager: CategoryManager
    lateinit var settingsManager: SettingsManager

    val isDarkTheme = mutableStateOf(false)
    val colorSourceState = mutableStateOf(ColorSource.MONET)
    val customColorSchemeState = mutableStateOf(CustomColorScheme.BLUE)

    override fun onCreate() {
        super.onCreate()
        instance = this
        repository = AppRepository(this)
        configManager = AppConfigManager(this)
        categoryManager = CategoryManager(this)
        settingsManager = SettingsManager(this)

        if (settingsManager.autoRotateColors && settingsManager.colorSource == ColorSource.CUSTOM) {
            val current = settingsManager.customColorScheme
            val choices = CustomColorScheme.entries.filter { it != current }
            choices.randomOrNull()?.let { settingsManager.customColorScheme = it }
        }

        syncThemeState()
    }

    fun syncThemeState() {
        isDarkTheme.value = settingsManager.isDarkTheme()
        colorSourceState.value = settingsManager.colorSource
        customColorSchemeState.value = settingsManager.customColorScheme
    }

    companion object {
        lateinit var instance: App
            private set
    }
}
