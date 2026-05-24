package com.sephuan.quicklaunch.service

import android.animation.ValueAnimator
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.sephuan.quicklaunch.App
import com.sephuan.quicklaunch.MainActivity
import com.sephuan.quicklaunch.R
import com.sephuan.quicklaunch.data.AppCustomConfig
import com.sephuan.quicklaunch.data.AppItem
import com.sephuan.quicklaunch.ui.components.AppIcon
import kotlinx.coroutines.*

class FloatingWindowService : LifecycleService(), ViewModelStoreOwner, SavedStateRegistryOwner {

    companion object {
        var isStarted = false
    }

    private lateinit var windowManager: WindowManager
    private lateinit var layoutParams: WindowManager.LayoutParams
    private lateinit var composeView: ComposeView

    private var isExpanded by mutableStateOf(false)
    private var screenWidth = 0
    private var bubbleSizePx = 0

    private var autoHideJob: Job? = null
    private var isHidden by mutableStateOf(false)
    private var isOnLeft by mutableStateOf(true)

    private val store = ViewModelStore()
    override val viewModelStore: ViewModelStore get() = store

    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    override fun onCreate() {
        super.onCreate()
        isStarted = true
        savedStateRegistryController.performRestore(null)

        startForegroundNotification()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        screenWidth = resources.displayMetrics.widthPixels
        bubbleSizePx = (60 * resources.displayMetrics.density).toInt()

        layoutParams = WindowManager.LayoutParams().apply {
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            }
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            format = PixelFormat.TRANSLUCENT
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            gravity = Gravity.TOP or Gravity.START
            x = 0
            y = 200
        }

        composeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@FloatingWindowService)
            setViewTreeViewModelStoreOwner(this@FloatingWindowService)
            setViewTreeSavedStateRegistryOwner(this@FloatingWindowService)
            setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_OUTSIDE) {
                    updateWindowMode(expanded = false)
                    true
                } else false
            }
        }

        windowManager.addView(composeView, layoutParams)
        setFloatingContent()
        composeView.post { snapToEdge() }
    }

    private fun startForegroundNotification() {
        val channelId = "floating_window"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "悬浮窗", NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val openIntent = PendingIntent.getActivity(this, 0,
            Intent(this, com.sephuan.quicklaunch.MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE)

        val searchPendingIntent = PendingIntent.getBroadcast(this, 1,
            Intent(this, SearchReceiver::class.java),
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val remoteInput = android.app.RemoteInput.Builder("search_query")
            .setLabel("搜索应用…")
            .build()

        val searchAction = Notification.Action.Builder(
            R.drawable.ic_tile_search, "搜索", searchPendingIntent
        ).addRemoteInput(remoteInput).build()

        val notification = Notification.Builder(this, channelId)
            .setContentTitle("QuickLaunch")
            .setContentText("悬浮搜索运行中 | 展开输入搜索")
            .setSmallIcon(R.drawable.ic_tile_search)
            .setContentIntent(openIntent)
            .setOngoing(true)
            .addAction(searchAction)
            .build()

        startForeground(1, notification)
    }

    private fun setFloatingContent() {
        composeView.setContent {
            val alpha by remember { derivedStateOf { if (isHidden) 0.5f else 1f } }

            FloatingWindowContent(
                isExpanded = isExpanded,
                alpha = alpha,
                onExpand = {
                    wakeUp()
                    updateWindowMode(expanded = true)
                },
                onCollapse = { updateWindowMode(expanded = false) },
                onClose = { stopSelf() },
                onMove = { dx, dy ->
                    if (!isExpanded) {
                        wakeUp()
                        val lp = this@FloatingWindowService.layoutParams
                        lp.x += dx.toInt()
                        lp.y += dy.toInt()
                        windowManager.updateViewLayout(composeView, lp)
                    }
                },
                onDragEnd = { snapToEdge() },
                onLaunchApp = { pkg ->
                    val app = applicationContext as App
                    app.configManager.recordLaunch(pkg)
                    val intent = packageManager.getLaunchIntentForPackage(pkg)
                    if (intent != null) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        updateWindowMode(expanded = false)
                    }
                }
            )
        }
    }

    private fun wakeUp() {
        autoHideJob?.cancel()
        if (isHidden) {
            isHidden = false
            val targetX = if (isOnLeft) 0 else screenWidth - bubbleSizePx
            animateToX(targetX)
        }
    }

    private fun snapToEdge() {
        if (isExpanded) return
        val currentX = layoutParams.x
        val centerX = currentX + bubbleSizePx / 2
        isOnLeft = centerX < screenWidth / 2
        val edgeX = if (isOnLeft) 0 else screenWidth - bubbleSizePx
        animateToX(edgeX)
        startAutoHideTimer()
    }

    private fun startAutoHideTimer() {
        autoHideJob?.cancel()
        autoHideJob = CoroutineScope(Dispatchers.Main).launch {
            delay(3000)
            if (!isExpanded) hideDeeply()
        }
    }

    private fun hideDeeply() {
        isHidden = true
        val hideOffset = (bubbleSizePx * 2) / 3
        val hideX = if (isOnLeft) -hideOffset else screenWidth - bubbleSizePx + hideOffset
        animateToX(hideX)
    }

    private fun animateToX(targetX: Int) {
        val startX = layoutParams.x
        val animator = ValueAnimator.ofInt(startX, targetX)
        animator.duration = 300
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            layoutParams.x = value
            try { windowManager.updateViewLayout(composeView, layoutParams) } catch (_: Exception) {}
        }
        animator.start()
    }

    private fun updateWindowMode(expanded: Boolean) {
        isExpanded = expanded
        wakeUp()

        if (expanded) {
            val cardWidthPx = (screenWidth - (32 * resources.displayMetrics.density).toInt())
            layoutParams.width = cardWidthPx
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
            layoutParams.x = 0
            layoutParams.y = (80 * resources.displayMetrics.density).toInt()
            layoutParams.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        } else {
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            layoutParams.gravity = Gravity.TOP or Gravity.START
            snapToEdge()
        }
        windowManager.updateViewLayout(composeView, layoutParams)
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        isStarted = false
        autoHideJob?.cancel()
        if (::windowManager.isInitialized && ::composeView.isInitialized) {
            try { windowManager.removeView(composeView) } catch (_: Exception) {}
        }
        store.clear()
    }
}

@Composable
fun FloatingWindowContent(
    isExpanded: Boolean,
    alpha: Float,
    onExpand: () -> Unit,
    onCollapse: () -> Unit,
    onClose: () -> Unit,
    onMove: (Float, Float) -> Unit,
    onDragEnd: () -> Unit,
    onLaunchApp: (String) -> Unit
) {
    if (isExpanded) {
        ExpandedSearchWindow(onCollapse, onLaunchApp)
    } else {
        Box(
            modifier = Modifier
                .size(60.dp)
                .alpha(alpha)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = onDragEnd,
                        onDrag = { change, dragAmount: Offset ->
                            change.consume()
                            onMove(dragAmount.x, dragAmount.y)
                        }
                    )
                }
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f))
                .clickable(onClick = onExpand),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = stringResource(R.string.search_placeholder),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun ExpandedSearchWindow(
    onCollapse: () -> Unit,
    onLaunchApp: (String) -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as App
    val configManager = app.configManager

    var searchQuery by remember { mutableStateOf("") }
    var allApps by remember { mutableStateOf<List<AppItem>>(emptyList()) }
    var configs by remember { mutableStateOf<Map<String, AppCustomConfig>>(emptyMap()) }

    LaunchedEffect(Unit) {
        val apps = app.repository.getInstalledApps()
        val loadedConfigs = configManager.getAllConfigs()
        allApps = apps
        configs = loadedConfigs
    }

    val displayedApps = remember(allApps, configs, searchQuery) {
        if (searchQuery.isBlank()) {
            allApps.filter { (configs[it.packageName]?.launchCount ?: 0) > 0 }
                .sortedByDescending { configs[it.packageName]?.launchCount }.take(5)
        } else {
            val query = searchQuery.trim()
            allApps.filter { a ->
                val config = configs[a.packageName]
                val customName = config?.customName ?: ""
                val tags = config?.tags ?: emptyList()
                a.label.contains(query, ignoreCase = true) ||
                    a.packageName.contains(query, ignoreCase = true) ||
                    customName.contains(query, ignoreCase = true) ||
                    tags.any { it.contains(query, ignoreCase = true) }
            }.sortedByDescending { a ->
                if (configs[a.packageName]?.customName?.contains(query, true) == true) 100 else 0
            }.take(6)
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp).heightIn(max = 400.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(stringResource(R.string.search_placeholder)) },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
                IconButton(onClick = onCollapse) {
                    Icon(Icons.Default.Close, contentDescription = stringResource(R.string.close))
                }
            }
            HorizontalDivider()
            LazyColumn {
                items(displayedApps) { a ->
                    val config = configs[a.packageName] ?: AppCustomConfig(a.packageName)
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { onLaunchApp(a.packageName) }.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppIcon(packageName = a.packageName, size = 32.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (config.customName.isNotBlank()) config.customName else a.label,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            if (config.tags.isNotEmpty()) {
                                Text(
                                    text = config.tags.joinToString(", "),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
