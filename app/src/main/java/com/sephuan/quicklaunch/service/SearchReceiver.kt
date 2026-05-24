package com.sephuan.quicklaunch.service

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sephuan.quicklaunch.App

class SearchReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val query = intent.getCharSequenceExtra("search_query")?.toString()?.trim() ?: return
        val app = context.applicationContext as App
        val configs = app.configManager.getAllConfigs()
        val pm = context.packageManager
        val launchIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val apps = pm.queryIntentActivities(launchIntent, 0)

        val best = apps.mapNotNull { ri ->
            val pkg = ri.activityInfo.packageName
            val label = ri.loadLabel(pm).toString()
            val cfg = configs[pkg]
            val alias = cfg?.customName ?: ""
            val tags = cfg?.tags ?: emptyList()

            var score = 0
            if (label.equals(query, true)) score += 100
            else if (label.contains(query, true)) score += 50
            if (alias.equals(query, true)) score += 90
            else if (alias.contains(query, true)) score += 60
            if (tags.any { it.equals(query, true) }) score += 80
            else if (tags.any { it.contains(query, true) }) score += 40
            if (pkg.contains(query, true)) score += 10
            Triple(pkg, label, score)
        }.filter { it.third > 0 }.sortedByDescending { it.third }

        if (best.isNotEmpty()) {
            val launchApp = pm.getLaunchIntentForPackage(best.first().first)
            if (launchApp != null) {
                launchApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchApp)
            }
        }
    }
}
