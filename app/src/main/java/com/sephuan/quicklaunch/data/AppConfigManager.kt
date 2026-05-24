package com.sephuan.quicklaunch.data

import android.content.Context
import com.google.gson.Gson

data class AppCustomConfig(
    val packageName: String,
    val customName: String = "",
    val tags: List<String> = emptyList(),
    val isPinned: Boolean = false,
    val launchCount: Int = 0,
    val lastLaunchTime: Long = 0
)

class AppConfigManager(context: Context) {
    private val prefs = context.getSharedPreferences("app_configs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveConfig(config: AppCustomConfig) {
        val json = gson.toJson(config)
        prefs.edit().putString(config.packageName, json).apply()
    }

    fun getConfig(packageName: String): AppCustomConfig {
        val json = prefs.getString(packageName, null)

        if (json != null) {
            // 1. 如果有用户保存的配置，直接用
            return try {
                gson.fromJson(json, AppCustomConfig::class.java)
            } catch (e: Exception) {
                AppCustomConfig(packageName)
            }
        } else {
            // 2. 如果没有用户配置，检查是否有默认配置
            val defaultTags = DefaultApps.mappings[packageName]
            return if (defaultTags != null) {
                // 返回带有默认标签的配置（暂不保存到本地，直到用户修改）
                AppCustomConfig(packageName = packageName, tags = defaultTags)
            } else {
                AppCustomConfig(packageName)
            }
        }
    }

    fun getAllConfigs(): Map<String, AppCustomConfig> {
        val allEntries = prefs.all
        val result = mutableMapOf<String, AppCustomConfig>()

        // 1. 读取所有已保存的用户配置
        for ((key, value) in allEntries) {
            if (value is String) {
                try {
                    val config = gson.fromJson(value, AppCustomConfig::class.java)
                    result[key] = config
                } catch (e: Exception) {
                    // ignore
                }
            }
        }
        return result
    }

    // 获取融合了默认配置的所有配置信息（用于分类界面）
    fun getAllConfigsWithDefaults(installedPackages: List<String>): Map<String, AppCustomConfig> {
        val userConfigs = getAllConfigs().toMutableMap()

        // 遍历所有已安装应用，如果用户没配置过，但有默认配置，就加进去
        installedPackages.forEach { packageName ->
            if (!userConfigs.containsKey(packageName)) {
                val defaultTags = DefaultApps.mappings[packageName]
                if (defaultTags != null) {
                    userConfigs[packageName] = AppCustomConfig(packageName, tags = defaultTags)
                }
            }
        }
        return userConfigs
    }

    fun recordLaunch(packageName: String) {
        val current = getConfig(packageName)
        val updated = current.copy(
            launchCount = current.launchCount + 1,
            lastLaunchTime = System.currentTimeMillis()
        )
        saveConfig(updated)
    }
}