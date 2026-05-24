package com.sephuan.quicklaunch.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.sephuan.quicklaunch.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AppIcon(
    packageName: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    val app = LocalContext.current.applicationContext as App
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(packageName) {
        bitmap = withContext(Dispatchers.IO) {
            try {
                app.packageManager.getApplicationIcon(packageName).toBitmap()
            } catch (_: Exception) {
                null
            }
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap!!.asImageBitmap(),
            contentDescription = null,
            modifier = modifier.size(size)
        )
    } else {
        Box(modifier = modifier.size(size))
    }
}
