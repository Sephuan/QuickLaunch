package com.sephuan.quicklaunch.data

import android.content.Context
import android.content.Intent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppRepository(private val context: Context) {

    suspend fun getInstalledApps(): List<AppItem> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)

        val apps = pm.queryIntentActivities(intent, 0)

        apps.map { resolveInfo ->
            AppItem(
                packageName = resolveInfo.activityInfo.packageName,
                label = resolveInfo.loadLabel(pm).toString()
            )
        }.sortedBy { it.label }
    }
}
