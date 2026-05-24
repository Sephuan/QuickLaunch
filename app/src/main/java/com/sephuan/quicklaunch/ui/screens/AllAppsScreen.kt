package com.sephuan.quicklaunch.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sephuan.quicklaunch.App
import com.sephuan.quicklaunch.R
import com.sephuan.quicklaunch.data.AppCustomConfig
import com.sephuan.quicklaunch.data.AppItem
import com.sephuan.quicklaunch.ui.components.AppIcon
import kotlinx.coroutines.launch

@Composable
fun AllAppsScreen() {
    val app = LocalContext.current.applicationContext as App
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val configManager = app.configManager

    var allApps by remember { mutableStateOf<List<AppItem>>(emptyList()) }
    var configs by remember { mutableStateOf<Map<String, AppCustomConfig>>(emptyMap()) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    var showDialog by remember { mutableStateOf(false) }
    var editingApp by remember { mutableStateOf<AppItem?>(null) }

    fun loadData() {
        scope.launch {
            val apps = app.repository.getInstalledApps()
            val loadedConfigs = configManager.getAllConfigs()
            allApps = apps
            configs = loadedConfigs
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { loadData() }

    val displayedApps = remember(allApps, configs, searchQuery) {
        if (searchQuery.isBlank()) allApps
        else {
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
                val config = configs[a.packageName]
                val customName = config?.customName ?: ""
                val tags = config?.tags ?: emptyList()
                val label = a.label
                var score = 0
                if (customName.equals(query, ignoreCase = true)) score += 100
                else if (customName.contains(query, ignoreCase = true)) score += 80
                if (tags.any { it.equals(query, ignoreCase = true) }) score += 90
                else if (tags.any { it.contains(query, ignoreCase = true) }) score += 70
                if (label.startsWith(query, ignoreCase = true)) score += 60
                else if (label.contains(query, ignoreCase = true)) score += 50
                if (a.packageName.contains(query, ignoreCase = true)) score += 10
                score
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            placeholder = { Text(stringResource(R.string.search_apps_alias_tags)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true
        )

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(bottom = 80.dp)) {
                items(displayedApps) { a ->
                    val config = configs[a.packageName] ?: AppCustomConfig(a.packageName)
                    AppListItem(
                        app = a,
                        config = config,
                        onClick = {
                            configManager.recordLaunch(a.packageName)
                            val intent = context.packageManager.getLaunchIntentForPackage(a.packageName)
                            intent?.let { context.startActivity(it) }
                        },
                        onLongClick = { editingApp = a; showDialog = true }
                    )
                }
            }
        }
    }

    if (showDialog && editingApp != null) {
        val currentConfig = configs[editingApp!!.packageName] ?: AppCustomConfig(editingApp!!.packageName)
        EditAppDialog(
            app = editingApp!!,
            currentConfig = currentConfig,
            onDismiss = { showDialog = false },
            onSave = { newConfig ->
                configManager.saveConfig(newConfig)
                configs = configManager.getAllConfigs()
                showDialog = false
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppListItem(
    app: AppItem,
    config: AppCustomConfig,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppIcon(packageName = app.packageName, size = 48.dp)
        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            val displayName = if (config.customName.isNotBlank()) config.customName else app.label
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = displayName, style = MaterialTheme.typography.bodyLarge)
                if (config.customName.isNotBlank()) {
                    Text(
                        text = " (${app.label})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            if (config.tags.isNotEmpty()) {
                Row(modifier = Modifier.padding(top = 4.dp)) {
                    config.tags.forEach { tag ->
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            } else {
                Text(text = app.packageName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}
