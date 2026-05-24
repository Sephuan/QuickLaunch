package com.sephuan.quicklaunch.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.UUID

data class AppCategory(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val packageNames: List<String> = emptyList()
)

class CategoryManager(private val context: Context) {
    private val prefs = context.getSharedPreferences("app_categories", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val KEY_CATEGORIES = "saved_categories"
    private val KEY_IS_INIT = "is_initialized"

    fun getCategories(): List<AppCategory> {
        val json = prefs.getString(KEY_CATEGORIES, null)
        return if (json != null) {
            val type = object : TypeToken<List<AppCategory>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    fun saveCategories(categories: List<AppCategory>) {
        val json = gson.toJson(categories)
        prefs.edit().putString(KEY_CATEGORIES, json).apply()
    }

    fun initDefaultCategoriesIfNeeded(installedApps: List<AppItem>) {
        if (prefs.getBoolean(KEY_IS_INIT, false)) return

        val defaultMap = mutableMapOf<String, MutableList<String>>()
        installedApps.forEach { app ->
            val defaultTags = DefaultApps.mappings[app.packageName]
            defaultTags?.forEach { tag ->
                if (!defaultMap.containsKey(tag)) {
                    defaultMap[tag] = mutableListOf()
                }
                defaultMap[tag]?.add(app.packageName)
            }
        }

        val initialCategories = defaultMap.map { (name, packages) ->
            AppCategory(name = name, packageNames = packages)
        }.sortedBy { it.name }

        saveCategories(initialCategories)
        prefs.edit().putBoolean(KEY_IS_INIT, true).apply()
    }

    fun updateCategory(category: AppCategory) {
        val list = getCategories().toMutableList()
        val index = list.indexOfFirst { it.id == category.id }
        if (index != -1) {
            list[index] = category
            saveCategories(list)
        }
    }

    fun addCategory(name: String) {
        val list = getCategories().toMutableList()
        list.add(AppCategory(name = name))
        saveCategories(list)
    }

    fun deleteCategory(categoryId: String) {
        val list = getCategories().toMutableList()
        list.removeAll { it.id == categoryId }
        saveCategories(list)
    }

    // --- 新增辅助方法：处理 App 的分类归属 ---

    // 获取某个 App 属于的所有分类 ID
    fun getCategoryIdsForApp(packageName: String): List<String> {
        return getCategories().filter { it.packageNames.contains(packageName) }.map { it.id }
    }

    // 更新某个 App 的分类归属
    // targetCategoryIds: 用户勾选的所有分类 ID
    fun updateAppCategories(packageName: String, targetCategoryIds: List<String>) {
        val allCategories = getCategories().toMutableList()

        val updatedCategories = allCategories.map { category ->
            val currentList = category.packageNames.toMutableList()
            val shouldBeIn = targetCategoryIds.contains(category.id)
            val isIn = currentList.contains(packageName)

            if (shouldBeIn && !isIn) {
                // 如果应该在，但不在 -> 添加
                currentList.add(packageName)
                category.copy(packageNames = currentList)
            } else if (!shouldBeIn && isIn) {
                // 如果不该在，但在 -> 删除
                currentList.remove(packageName)
                category.copy(packageNames = currentList)
            } else {
                category
            }
        }

        saveCategories(updatedCategories)
    }
    fun removeAppFromCategory(categoryId: String, packageName: String) {
        val allCategories = getCategories().toMutableList()
        val index = allCategories.indexOfFirst { it.id == categoryId }

        if (index != -1) {
            val category = allCategories[index]
            // 创建一个新的列表，排除掉要删除的包名
            val newPackageNames = category.packageNames.filter { it != packageName }

            // 更新分类
            allCategories[index] = category.copy(packageNames = newPackageNames)
            saveCategories(allCategories)
        }
    }
}