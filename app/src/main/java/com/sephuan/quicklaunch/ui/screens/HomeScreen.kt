package com.sephuan.quicklaunch.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sephuan.quicklaunch.App
import com.sephuan.quicklaunch.R
import com.sephuan.quicklaunch.data.AppCustomConfig
import com.sephuan.quicklaunch.data.AppItem
import com.sephuan.quicklaunch.ui.components.AppIcon
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(onSettingsClick: () -> Unit = {}) {
    val app = LocalContext.current.applicationContext as App
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val configManager = app.configManager

    var pinnedApps by remember { mutableStateOf<List<Pair<AppItem, AppCustomConfig>>>(emptyList()) }
    var frequentApps by remember { mutableStateOf<List<Pair<AppItem, AppCustomConfig>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    var showEditDialog by remember { mutableStateOf(false) }
    var editingApp by remember { mutableStateOf<AppItem?>(null) }

    fun loadData() {
        scope.launch {
            val allApps = app.repository.getInstalledApps()
            val configs = configManager.getAllConfigs()
            val appMap = allApps.associateBy { it.packageName }

            pinnedApps = configs.values
                .filter { it.isPinned }
                .mapNotNull { config -> appMap[config.packageName]?.let { it to config } }

            frequentApps = configs.values
                .filter { it.launchCount > 0 && !it.isPinned }
                .sortedByDescending { it.launchCount }
                .take(10)
                .mapNotNull { config -> appMap[config.packageName]?.let { it to config } }

            isLoading = false
        }
    }

    LaunchedEffect(Unit) { loadData() }

    if (showEditDialog && editingApp != null) {
        val currentConfig = configManager.getConfig(editingApp!!.packageName)
        EditAppDialog(
            app = editingApp!!,
            currentConfig = currentConfig,
            onDismiss = { showEditDialog = false },
            onSave = { newConfig ->
                configManager.saveConfig(newConfig)
                showEditDialog = false
                loadData()
            }
        )
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.home),
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            item { FloatingWindowSwitchCard(context) }

            if (pinnedApps.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.my_pins),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                item {
                    PinnedAppsGrid(
                        apps = pinnedApps,
                        onAppClick = { packageName ->
                            configManager.recordLaunch(packageName)
                            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
                            intent?.let { context.startActivity(it) }
                        },
                        onAppLongClick = { editingApp = it; showEditDialog = true }
                    )
                }
            } else {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Text(
                            text = stringResource(R.string.pin_hint),
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            item {
                Text(
                    text = stringResource(R.string.frequently_used),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp, 24.dp, 16.dp, 8.dp)
                )
            }

            if (frequentApps.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.frequent_hint),
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            } else {
                items(frequentApps) { (a, c) ->
                    AppListItem(
                        app = a,
                        config = c,
                        onClick = {
                            configManager.recordLaunch(a.packageName)
                            val intent = context.packageManager.getLaunchIntentForPackage(a.packageName)
                            intent?.let { context.startActivity(it) }
                        },
                        onLongClick = { editingApp = a; showEditDialog = true }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PinnedAppsGrid(
    apps: List<Pair<AppItem, AppCustomConfig>>,
    onAppClick: (String) -> Unit,
    onAppLongClick: (AppItem) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
        val chunkedApps = apps.chunked(4)
        chunkedApps.forEach { rowApps ->
            Row(modifier = Modifier.fillMaxWidth()) {
                rowApps.forEach { (a, config) ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                            .combinedClickable(
                                onClick = { onAppClick(a.packageName) },
                                onLongClick = { onAppLongClick(a) }
                            )
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AppIcon(packageName = a.packageName, size = 56.dp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (config.customName.isNotBlank()) config.customName else a.label,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                repeat(4 - rowApps.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun FloatingWindowSwitchCard(context: android.content.Context) {
    var hasPermission by remember { mutableStateOf(android.provider.Settings.canDrawOverlays(context)) }
    var isServiceActive by remember { mutableStateOf(hasPermission) }

    val lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                val granted = android.provider.Settings.canDrawOverlays(context)
                hasPermission = granted
                if (granted) {
                    isServiceActive = true
                    val intent = android.content.Intent(context, com.sephuan.quicklaunch.service.FloatingWindowService::class.java)
                    context.startService(intent)
                } else {
                    isServiceActive = false
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.floating_search),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = if (hasPermission) stringResource(R.string.floating_enabled_desc) else stringResource(R.string.floating_disabled_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
            Switch(
                checked = isServiceActive,
                onCheckedChange = { checked ->
                    if (checked) {
                        if (!android.provider.Settings.canDrawOverlays(context)) {
                            val intent = android.content.Intent(
                                android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                android.net.Uri.parse("package:${context.packageName}")
                            )
                            context.startActivity(intent)
                        } else {
                            val intent = android.content.Intent(context, com.sephuan.quicklaunch.service.FloatingWindowService::class.java)
                            context.startService(intent)
                            isServiceActive = true
                        }
                    } else {
                        val intent = android.content.Intent(context, com.sephuan.quicklaunch.service.FloatingWindowService::class.java)
                        context.stopService(intent)
                        isServiceActive = false
                    }
                }
            )
        }
    }
}
