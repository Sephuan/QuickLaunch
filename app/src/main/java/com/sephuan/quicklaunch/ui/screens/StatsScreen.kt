package com.sephuan.quicklaunch.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sephuan.quicklaunch.App
import com.sephuan.quicklaunch.R
import com.sephuan.quicklaunch.data.AppCustomConfig
import com.sephuan.quicklaunch.data.AppItem
import com.sephuan.quicklaunch.ui.components.AppIcon
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StatsScreen(onSettingsClick: () -> Unit = {}) {
    val app = LocalContext.current.applicationContext as App
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val configManager = app.configManager

    var sortedApps by remember { mutableStateOf<List<Pair<AppItem, AppCustomConfig>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    var showEditDialog by remember { mutableStateOf(false) }
    var editingApp by remember { mutableStateOf<AppItem?>(null) }

    fun loadData() {
        scope.launch {
            val allApps = app.repository.getInstalledApps()
            val configs = configManager.getAllConfigs()
            val appMap = allApps.associateBy { it.packageName }

            sortedApps = configs.values
                .filter { it.launchCount > 0 }
                .sortedByDescending { it.launchCount }
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

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.usage_stats),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings))
            }
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (sortedApps.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.no_stats))
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(bottom = 80.dp)) {
                itemsIndexed(sortedApps) { index, (item, config) ->
                    StatsItem(
                        rank = index + 1,
                        app = item,
                        config = config,
                        onClick = {
                            configManager.recordLaunch(item.packageName)
                            val intent = context.packageManager.getLaunchIntentForPackage(item.packageName)
                            intent?.let { context.startActivity(it) }
                        },
                        onLongClick = {
                            editingApp = item
                            showEditDialog = true
                        }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StatsItem(
    rank: Int,
    app: AppItem,
    config: AppCustomConfig,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "#$rank",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.width(40.dp)
        )

        AppIcon(packageName = app.packageName, size = 40.dp)
        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (config.customName.isNotBlank()) config.customName else app.label,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = stringResource(R.string.last_launch) + ": " + SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(Date(config.lastLaunchTime)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = "${config.launchCount} ${stringResource(R.string.times)}",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}
