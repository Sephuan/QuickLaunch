package com.sephuan.quicklaunch.service

import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.graphics.drawable.Icon
import com.sephuan.quicklaunch.App
import com.sephuan.quicklaunch.R

open class QuickTileBaseService(val slotId: Int) : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        refreshTile()
    }

    override fun onClick() {
        super.onClick()
        val app = applicationContext as App
        val slot = app.settingsManager.getTileSlot(slotId)
        if (slot == null || slot.packageName.isEmpty()) {
            launchAndCollapse(Intent(this, com.sephuan.quicklaunch.MainActivity::class.java))
            return
        }
        val intent = packageManager.getLaunchIntentForPackage(slot.packageName)
        if (intent != null) {
            launchAndCollapse(intent)
        } else {
            launchAndCollapse(Intent(this, com.sephuan.quicklaunch.MainActivity::class.java))
        }
    }

    private fun launchAndCollapse(intent: Intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        try {
            sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
        } catch (_: Exception) {}
    }

    private fun refreshTile() {
        val tile = qsTile ?: return
        val app = applicationContext as App
        val slot = app.settingsManager.getTileSlot(slotId)

        tile.state = if (slot != null && slot.packageName.isNotEmpty()) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.label = slot?.label?.ifBlank { getAppLabel(slot.packageName) }
            ?: getString(R.string.tile_slot_empty, slotId + 1)

        val iconRes = when {
            slot != null && slot.iconResId != 0 -> slot.iconResId
            else -> R.drawable.ic_tile_shortcut
        }
        tile.icon = Icon.createWithResource(this, iconRes)
        tile.updateTile()
    }

    private fun getAppLabel(pkg: String): String? = try {
        packageManager.getApplicationLabel(packageManager.getApplicationInfo(pkg, 0)).toString()
    } catch (_: Exception) { pkg }
}

class QuickTileService1 : QuickTileBaseService(0)
class QuickTileService2 : QuickTileBaseService(1)
class QuickTileService3 : QuickTileBaseService(2)
class QuickTileService4 : QuickTileBaseService(3)
class QuickTileService5 : QuickTileBaseService(4)
class QuickTileService6 : QuickTileBaseService(5)
class QuickTileService7 : QuickTileBaseService(6)
class QuickTileService8 : QuickTileBaseService(7)
class QuickTileService9 : QuickTileBaseService(8)
class QuickTileService10 : QuickTileBaseService(9)
class QuickTileService11 : QuickTileBaseService(10)
class QuickTileService12 : QuickTileBaseService(11)
