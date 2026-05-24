package com.sephuan.quicklaunch.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sephuan.quicklaunch.App
import com.sephuan.quicklaunch.R
import com.sephuan.quicklaunch.data.AppCustomConfig
import com.sephuan.quicklaunch.data.AppItem
import com.sephuan.quicklaunch.data.ColorSource
import com.sephuan.quicklaunch.data.CustomColorScheme
import com.sephuan.quicklaunch.data.QuickTileSlot
import com.sephuan.quicklaunch.data.SettingsManager
import com.sephuan.quicklaunch.data.ThemeMode
import com.sephuan.quicklaunch.ui.components.AppIcon
import com.sephuan.quicklaunch.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit, onAboutClick: (() -> Unit)? = null) {
    val app = LocalContext.current.applicationContext as App
    val settings = app.settingsManager

    var themeMode by remember { mutableStateOf(settings.themeMode) }
    var colorSource by remember { mutableStateOf(settings.colorSource) }
    var customScheme by remember { mutableStateOf(settings.customColorScheme) }
    var language by remember { mutableStateOf(settings.language) }
    var immersive by remember { mutableStateOf(settings.immersiveMode) }
    var autoRotate by remember { mutableStateOf(settings.autoRotateColors) }
    var showTileDialog by remember { mutableIntStateOf(-1) }

    var expandedLang by remember { mutableStateOf(false) }
    var expandedTheme by remember { mutableStateOf(false) }
    var expandedColor by remember { mutableStateOf(false) }
    var expandedTiles by remember { mutableStateOf(false) }
    var visibleTileCount by remember { mutableIntStateOf(
        (0..11).count { settings.getTileSlot(it)?.packageName?.isNotEmpty() == true }.coerceAtLeast(1)
    ) }

    val tileSlots = remember {
        val slots = mutableStateListOf<QuickTileSlot?>()
        for (i in 0..11) slots.add(settings.getTileSlot(i))
        slots
    }
    fun refreshTiles() {
        for (i in 0..11) { if (i < tileSlots.size) tileSlots[i] = settings.getTileSlot(i) else tileSlots.add(settings.getTileSlot(i)) }
        visibleTileCount = (0..11).count { settings.getTileSlot(it)?.packageName?.isNotEmpty() == true }.coerceAtLeast(1)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
        ) {
            // Language
            SettingsExpandHeader(stringResource(R.string.language), expandedLang) { expandedLang = !expandedLang }
            AnimatedVisibility(expandedLang) {
                Column(modifier = Modifier.padding(bottom = 8.dp)) {
                    SelectionChipRow(
                        options = listOf("auto" to stringResource(R.string.language_auto), "zh" to stringResource(R.string.language_zh), "en" to stringResource(R.string.language_en)),
                        selected = language,
                        onSelect = { language = it; settings.language = it }
                    )
                }
            }
            HorizontalDivider()

            // Theme
            SettingsExpandHeader(stringResource(R.string.theme_mode), expandedTheme) { expandedTheme = !expandedTheme }
            AnimatedVisibility(expandedTheme) {
                Column(modifier = Modifier.padding(bottom = 8.dp)) {
                    SelectionChipRow(
                        options = listOf(ThemeMode.SYSTEM.name to stringResource(R.string.theme_system), ThemeMode.LIGHT.name to stringResource(R.string.theme_light), ThemeMode.DARK.name to stringResource(R.string.theme_dark)),
                        selected = themeMode.name,
                        onSelect = { key -> themeMode = ThemeMode.valueOf(key); settings.themeMode = ThemeMode.valueOf(key) }
                    )
                }
            }
            HorizontalDivider()

            // Color
            SettingsExpandHeader(stringResource(R.string.color_source), expandedColor) { expandedColor = !expandedColor }
            AnimatedVisibility(expandedColor) {
                Column(modifier = Modifier.padding(bottom = 8.dp)) {
                    SelectionChipRow(
                        options = listOf(ColorSource.MONET.name to stringResource(R.string.color_monet), ColorSource.CUSTOM.name to stringResource(R.string.color_custom)),
                        selected = colorSource.name,
                        onSelect = { key -> colorSource = ColorSource.valueOf(key); settings.colorSource = ColorSource.valueOf(key) }
                    )
                    AnimatedVisibility(colorSource == ColorSource.CUSTOM) {
                        Column {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(stringResource(R.string.custom_color_scheme), style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(horizontal = 16.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(ColorSchemeItem.all) { item ->
                                    ColorSwatch(item = item, selected = customScheme == item.scheme, onClick = { customScheme = item.scheme; settings.customColorScheme = item.scheme })
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            SettingsSwitchItem(stringResource(R.string.auto_rotate_colors), stringResource(R.string.auto_rotate_desc), autoRotate) { autoRotate = it; settings.autoRotateColors = it }
                        }
                    }
                }
            }
            HorizontalDivider()

            // Immersive
            SettingsSwitchItem(stringResource(R.string.immersive_mode), stringResource(R.string.immersive_desc), immersive) { immersive = it; settings.immersiveMode = it }
            HorizontalDivider()

            // Quick Tiles
            SettingsExpandHeader(stringResource(R.string.quick_tiles), expandedTiles) { expandedTiles = !expandedTiles }
            AnimatedVisibility(expandedTiles) {
                Column(modifier = Modifier.padding(bottom = 8.dp)) {
                    tileSlots.take(visibleTileCount).forEachIndexed { index, slot ->
                        val hasApp = slot != null && slot.packageName.isNotEmpty()
                        val alias = if (hasApp) (app.configManager.getConfig(slot!!.packageName).customName.ifBlank { slot.label }) else null
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { showTileDialog = index }.padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (hasApp) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(Modifier.size(36.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            if (slot!!.iconResId != 0)
                                                Icon(painterResource(id = slot!!.iconResId), null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(20.dp))
                                            else
                                                AppIcon(packageName = slot!!.packageName, size = 36.dp)
                                        }
                                    }
                                    Spacer(Modifier.width(6.dp))
                                    AppIcon(packageName = slot!!.packageName, size = 36.dp)
                                }
                            } else {
                                Surface(Modifier.size(40.dp), shape = CircleShape, color = MaterialTheme.colorScheme.surfaceVariant) {
                                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp))
                                    }
                                }
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                if (hasApp) {
                                    val name = alias ?: slot!!.label
                                    Text(name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                    if (alias != null && alias != slot!!.label) Text(slot!!.label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                                    Text("#${index + 1} · ${slot!!.packageName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                                } else {
                                    Text(stringResource(R.string.tile_slot_empty), style = MaterialTheme.typography.bodyMedium)
                                    Text(stringResource(R.string.tap_to_configure), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                                }
                            }
                            if (hasApp) {
                                IconButton(onClick = { settings.removeTileSlot(index); refreshTiles() }, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                        if (index < visibleTileCount - 1) HorizontalDivider(modifier = Modifier.padding(start = 56.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    }
                    if (visibleTileCount < 12) {
                        TextButton(
                            onClick = { visibleTileCount++ },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Icon(Icons.Default.Add, null, Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.add_tile))
                        }
                    }
                }
            }
            HorizontalDivider()

            // About
            SettingsExpandHeader(stringResource(R.string.about), false) {}
            ListItem(
                headlineContent = { Text(stringResource(R.string.app_name)) },
                supportingContent = { Text("v1.0 · MIT License") },
                leadingContent = { Icon(Icons.Default.Info, contentDescription = null) },
                modifier = Modifier.clickable { onAboutClick?.invoke() }
            )
        }
    }

    if (showTileDialog >= 0) {
        val tileApps = remember { mutableStateListOf<AppItem>() }
        LaunchedEffect(Unit) { tileApps.addAll(app.repository.getInstalledApps()) }
        TileConfigDialog(slotId = showTileDialog, settings = settings, allApps = tileApps, onDismiss = { showTileDialog = -1; refreshTiles() })
    }
}

@Composable
fun SettingsExpandHeader(title: String, expanded: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onToggle).padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))
        Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun SelectionChipRow(options: List<Pair<String, String>>, selected: String, onSelect: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { (key, label) ->
            FilterChip(selected = key == selected, onClick = { onSelect(key) }, label = { Text(label) })
        }
    }
}

data class ColorSchemeItem(val scheme: CustomColorScheme, val nameRes: Int, val primaryColor: Color, val surfaceColor: Color) {
    companion object {
        val all = listOf(
            ColorSchemeItem(CustomColorScheme.BLUE, R.string.color_blue, BlueLightPrimary, BlueLightSurface),
            ColorSchemeItem(CustomColorScheme.GREEN, R.string.color_green, GreenLightPrimary, BlueLightSurface),
            ColorSchemeItem(CustomColorScheme.ORANGE, R.string.color_orange, OrangeLightPrimary, BlueLightSurface),
            ColorSchemeItem(CustomColorScheme.ROSE, R.string.color_rose, RoseLightPrimary, BlueLightSurface),
            ColorSchemeItem(CustomColorScheme.VIOLET, R.string.color_violet, VioletLightPrimary, BlueLightSurface),
            ColorSchemeItem(CustomColorScheme.TEAL, R.string.color_teal, TealLightPrimary, BlueLightSurface),
            ColorSchemeItem(CustomColorScheme.AMBER, R.string.color_amber, AmberLightPrimary, BlueLightSurface),
            ColorSchemeItem(CustomColorScheme.INDIGO, R.string.color_indigo, IndigoLightPrimary, BlueLightSurface)
        )
    }
}

@Composable
fun ColorSwatch(item: ColorSchemeItem, selected: Boolean, onClick: () -> Unit) {
    Column(modifier = Modifier.width(60.dp).clickable(onClick = onClick), horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(44.dp),
            shape = CircleShape,
            color = item.primaryColor,
            shadowElevation = if (selected) 4.dp else 1.dp,
            border = if (selected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (selected) Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(24.dp))
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(stringResource(item.nameRes), style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal), maxLines = 1, color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun SettingsSwitchItem(title: String, subtitle: String?, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            if (subtitle != null) Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

val tileIconOptions = listOf(
    R.drawable.ic_tile_search, R.drawable.ic_tile_star, R.drawable.ic_tile_heart,
    R.drawable.ic_tile_play, R.drawable.ic_tile_lightning, R.drawable.ic_tile_bolt,
    R.drawable.ic_tile_chart, R.drawable.ic_tile_music, R.drawable.ic_tile_bookmark,
    R.drawable.ic_tile_gear, R.drawable.ic_tile_folder, R.drawable.ic_tile_camera,
    R.drawable.ic_tile_rocket, R.drawable.ic_tile_shortcut
)

@Composable
fun TileConfigDialog(slotId: Int, settings: SettingsManager, allApps: List<AppItem>, onDismiss: () -> Unit) {
    val app = LocalContext.current.applicationContext as App
    val slot = settings.getTileSlot(slotId)
    var selectedPackage by remember { mutableStateOf(slot?.packageName ?: "") }
    var selectedIcon by remember { mutableIntStateOf(slot?.iconResId ?: 0) }
    var searchQuery by remember { mutableStateOf("") }

    val configs = remember { mutableStateMapOf<String, AppCustomConfig>() }
    LaunchedEffect(Unit) { configs.putAll(app.configManager.getAllConfigs()) }

    val filteredApps = remember(allApps, configs, searchQuery) {
        val filtered = if (searchQuery.isBlank()) allApps
        else {
            val q = searchQuery.trim()
            allApps.filter { a ->
                val cfg = configs[a.packageName]
                val alias = cfg?.customName ?: ""
                val tags = cfg?.tags ?: emptyList()
                a.label.contains(q, true) || a.packageName.contains(q, true) || alias.contains(q, true) || tags.any { it.contains(q, true) }
            }
        }
        val limited = filtered.take(50).toMutableList()
        if (selectedPackage.isNotEmpty() && limited.none { it.packageName == selectedPackage }) {
            allApps.find { it.packageName == selectedPackage }?.let { limited.add(0, it) }
        }
        limited
    }

    AlertDialog(
    onDismissRequest = onDismiss,
    title = {
        Column {
            Text(stringResource(R.string.configure_tile))
            if (selectedPackage.isNotEmpty()) {
                val label = allApps.find { it.packageName == selectedPackage }?.label ?: selectedPackage
                val cfg = configs[selectedPackage]
                val alias = cfg?.customName?.takeIf { it.isNotBlank() }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(Modifier.size(24.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            if (selectedIcon != 0) Icon(painterResource(id = selectedIcon), null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(14.dp))
                        }
                    }
                    Spacer(Modifier.width(6.dp))
                    AppIcon(packageName = selectedPackage, size = 24.dp)
                    Spacer(Modifier.width(8.dp))
                    Text(if (alias != null) "$alias ($label)" else label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    },
        text = {
            Column(Modifier.fillMaxWidth().heightIn(max = 420.dp).verticalScroll(rememberScrollState())) {
                OutlinedTextField(value = searchQuery, onValueChange = { searchQuery = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text(stringResource(R.string.search_apps)) }, singleLine = true)
                Spacer(Modifier.height(8.dp))
                LazyColumn(Modifier.heightIn(max = 200.dp)) {
                    items(filteredApps) { a ->
                        Row(Modifier.fillMaxWidth().clickable { selectedPackage = a.packageName }.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = selectedPackage == a.packageName, onClick = { selectedPackage = a.packageName })
                            Spacer(Modifier.width(8.dp))
                            AppIcon(packageName = a.packageName, size = 32.dp)
                            Spacer(Modifier.width(8.dp))
                            Text(a.label, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text(stringResource(R.string.select_icon), style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(tileIconOptions) { iconId ->
                        val isSelected = selectedIcon == iconId
                        Surface(Modifier.size(40.dp).clickable { selectedIcon = iconId }, shape = CircleShape, color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant, border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Icon(painterResource(id = iconId), null, tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(22.dp))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (selectedPackage.isNotBlank()) {
                    val label = allApps.find { it.packageName == selectedPackage }?.label ?: ""
                    settings.saveTileSlot(QuickTileSlot(slotId = slotId, packageName = selectedPackage, label = label, iconResId = selectedIcon))
                }
                onDismiss()
            }) { Text(stringResource(R.string.save)) }
        },
        dismissButton = {
            Row {
                if (slot != null && slot.packageName.isNotEmpty()) {
                    TextButton(onClick = { settings.removeTileSlot(slotId); onDismiss() }) { Text(stringResource(R.string.clear_tile), color = MaterialTheme.colorScheme.error) }
                }
                TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
            }
        }
    )
}
