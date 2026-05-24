package com.sephuan.quicklaunch.service

import android.content.Intent
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast
import com.sephuan.quicklaunch.R

class QuickSettingTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        updateTileState()
    }

    override fun onClick() {
        super.onClick()

        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, getString(R.string.floating_disabled_desc), Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivityAndCollapse(intent)
            return
        }

        val intent = Intent(this, FloatingWindowService::class.java)
        if (FloatingWindowService.isStarted) {
            stopService(intent)
            qsTile.state = Tile.STATE_INACTIVE
        } else {
            startService(intent)
            qsTile.state = Tile.STATE_ACTIVE
        }
        qsTile.updateTile()
    }

    private fun updateTileState() {
        val tile = qsTile ?: return
        tile.state = if (FloatingWindowService.isStarted) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.updateTile()
    }
}
