package com.sephuan.quicklaunch.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.sephuan.quicklaunch.App
import com.sephuan.quicklaunch.R
import com.sephuan.quicklaunch.data.*
import com.sephuan.quicklaunch.ui.components.AppIcon
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class CategoryUIState {
    VIEW, MANAGE, SELECT_APPS
}

@Composable
fun CategoryScreen() {
    val appCtx = LocalContext.current.applicationContext as App
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val configManager = appCtx.configManager
    val categoryManager = appCtx.categoryManager

    var categories by remember { mutableStateOf<List<AppCategory>>(emptyList()) }
    var allApps by remember { mutableStateOf<List<AppItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    var uiState by remember { mutableStateOf(CategoryUIState.VIEW) }
    var currentEditingCategory by remember { mutableStateOf<AppCategory?>(null) }

    var showEditAppDialog by remember { mutableStateOf(false) }
    var editingApp by remember { mutableStateOf<AppItem?>(null) }

    fun loadData() {
        scope.launch {
            val apps = appCtx.repository.getInstalledApps()
            allApps = apps
            categoryManager.initDefaultCategoriesIfNeeded(apps)
            categories = categoryManager.getCategories()
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { loadData() }

    if (showEditAppDialog && editingApp != null) {
        val currentConfig = configManager.getConfig(editingApp!!.packageName)
        EditAppDialog(
            app = editingApp!!,
            currentConfig = currentConfig,
            onDismiss = { showEditAppDialog = false },
            onSave = { newConfig ->
                configManager.saveConfig(newConfig)
                showEditAppDialog = false
                loadData()
            }
        )
    }

    when (uiState) {
        CategoryUIState.VIEW -> CategoryViewScreen(
            categories = categories,
            allApps = allApps,
            configManager = configManager,
            isLoading = isLoading,
            onManageClick = { uiState = CategoryUIState.MANAGE },
            onAppClick = { pkg ->
                configManager.recordLaunch(pkg)
                val intent = context.packageManager.getLaunchIntentForPackage(pkg)
                intent?.let { context.startActivity(it) }
            },
            onEditAppRequest = { editingApp = it; showEditAppDialog = true },
            onRemoveAppFromCategory = { catId, pkg ->
                categoryManager.removeAppFromCategory(catId, pkg)
                loadData()
            },
            onAddAppsClick = { currentEditingCategory = it; uiState = CategoryUIState.SELECT_APPS }
        )
        CategoryUIState.MANAGE -> CategoryManageScreen(
            categories = categories,
            onBack = { loadData(); uiState = CategoryUIState.VIEW },
            onUpdateCategories = { newCats ->
                categoryManager.saveCategories(newCats)
                categories = newCats
            },
            onEditContent = { currentEditingCategory = it; uiState = CategoryUIState.SELECT_APPS }
        )
        CategoryUIState.SELECT_APPS -> {
            if (currentEditingCategory != null) {
                AppSelectionScreen(
                    category = currentEditingCategory!!,
                    allApps = allApps,
                    onBack = { loadData(); uiState = CategoryUIState.VIEW },
                    onSave = { updated ->
                        categoryManager.updateCategory(updated)
                        categories = categories.map { if (it.id == updated.id) updated else it }
                        currentEditingCategory = updated
                    }
                )
            } else { uiState = CategoryUIState.MANAGE }
        }
    }
}

@Composable
fun CategoryViewScreen(
    categories: List<AppCategory>,
    allApps: List<AppItem>,
    configManager: AppConfigManager,
    isLoading: Boolean,
    onManageClick: () -> Unit,
    onAppClick: (String) -> Unit,
    onEditAppRequest: (AppItem) -> Unit,
    onRemoveAppFromCategory: (String, String) -> Unit,
    onAddAppsClick: (AppCategory) -> Unit
) {
    val appMap = remember(allApps) { allApps.associateBy { it.packageName } }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(R.string.category_browse), style = MaterialTheme.typography.headlineMedium)
                Button(onClick = onManageClick) {
                    Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.manage))
                }
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else if (categories.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.no_categories))
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(bottom = 80.dp, top = padding.calculateTopPadding())) {
                items(categories) { category ->
                    val categoryApps = category.packageNames
                        .mapNotNull { pkg -> appMap[pkg] }
                        .sortedByDescending { configManager.getConfig(it.packageName).launchCount }

                    CategoryDisplaySection(
                        category = category,
                        apps = categoryApps,
                        configManager = configManager,
                        onAppClick = onAppClick,
                        onEditAppRequest = onEditAppRequest,
                        onRemoveApp = { pkg -> onRemoveAppFromCategory(category.id, pkg) },
                        onAddClick = { onAddAppsClick(category) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoryDisplaySection(
    category: AppCategory,
    apps: List<AppItem>,
    configManager: AppConfigManager,
    onAppClick: (String) -> Unit,
    onEditAppRequest: (AppItem) -> Unit,
    onRemoveApp: (String) -> Unit,
    onAddClick: () -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "(${apps.size})",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(apps) { a ->
                val config = configManager.getConfig(a.packageName)
                var showMenu by remember { mutableStateOf(false) }

                Box {
                    Column(
                        modifier = Modifier
                            .width(72.dp)
                            .combinedClickable(
                                onClick = { onAppClick(a.packageName) },
                                onLongClick = { showMenu = true }
                            )
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AppIcon(packageName = a.packageName, size = 48.dp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (config.customName.isNotBlank()) config.customName else a.label,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.remove_from_category), color = MaterialTheme.colorScheme.error) },
                            onClick = { onRemoveApp(a.packageName); showMenu = false },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.edit_app)) },
                            onClick = { onEditAppRequest(a); showMenu = false },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                        )
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .width(72.dp)
                        .padding(vertical = 8.dp)
                        .clickable { onAddClick() },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add), tint = MaterialTheme.colorScheme.onSecondaryContainer)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = stringResource(R.string.add), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                }
            }
        }
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CategoryManageScreen(
    categories: List<AppCategory>,
    onBack: () -> Unit,
    onUpdateCategories: (List<AppCategory>) -> Unit,
    onEditContent: (AppCategory) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }
    var showRenameDialog by remember { mutableStateOf(false) }
    var renamingCategory by remember { mutableStateOf<AppCategory?>(null) }
    var renameText by remember { mutableStateOf("") }

    var draggedIndex by remember { mutableIntStateOf(-1) }
    var dragOffsetY by remember { mutableFloatStateOf(0f) }

    val itemHeightPx = with(LocalContext.current.resources.displayMetrics) { (72f * density).roundToInt() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.manage_categories)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = stringResource(R.string.new_item))
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            itemsIndexed(categories, key = { _, c -> c.id }) { index, category ->
                val isDragging = draggedIndex == index
                val elevation by animateDpAsState(if (isDragging) 8.dp else 0.dp, label = "elev")

                Box(
                    modifier = Modifier
                        .zIndex(if (isDragging) 1f else 0f)
                        .graphicsLayer {
                            translationY = if (isDragging) dragOffsetY else 0f
                        }
                        .shadow(elevation)
                        .background(MaterialTheme.colorScheme.surface)
                        .pointerInput(index) {
                            detectDragGesturesAfterLongPress(
                                onDragStart = { draggedIndex = index; dragOffsetY = 0f },
                                onDragEnd = {
                                    val targetIdx = (index + (dragOffsetY / itemHeightPx).roundToInt())
                                        .coerceIn(0, categories.size - 1)
                                    if (targetIdx != index) {
                                        val newList = categories.toMutableList()
                                        val moved = newList.removeAt(index)
                                        newList.add(targetIdx, moved)
                                        onUpdateCategories(newList)
                                    }
                                    draggedIndex = -1
                                    dragOffsetY = 0f
                                },
                                onDragCancel = { draggedIndex = -1; dragOffsetY = 0f },
                                onDrag = { _, amount -> dragOffsetY += amount.y }
                            )
                        }
                ) {
                    ListItem(
                        headlineContent = {
                            Text(category.name, style = MaterialTheme.typography.titleMedium)
                        },
                        supportingContent = {
                            Text(stringResource(R.string.apps_count, category.packageNames.size))
                        },
                        leadingContent = {
                            Icon(
                                Icons.Default.DragHandle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        trailingContent = {
                            Row {
                                IconButton(onClick = { onEditContent(category) }) {
                                    Icon(Icons.Default.Apps, contentDescription = stringResource(R.string.edit_content), tint = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(onClick = {
                                    renamingCategory = category
                                    renameText = category.name
                                    showRenameDialog = true
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.rename_category), tint = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(onClick = {
                                    val newList = categories.toMutableList()
                                    newList.removeAt(index)
                                    onUpdateCategories(newList)
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete), tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    )
                }
                HorizontalDivider()
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(stringResource(R.string.new_category)) },
            text = {
                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    label = { Text(stringResource(R.string.category_name)) },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (newCategoryName.isNotBlank()) {
                        val newList = categories.toMutableList()
                        newList.add(AppCategory(name = newCategoryName.trim()))
                        onUpdateCategories(newList)
                        newCategoryName = ""
                        showAddDialog = false
                    }
                }) { Text(stringResource(R.string.confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }

    if (showRenameDialog && renamingCategory != null) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text(stringResource(R.string.rename_category)) },
            text = {
                OutlinedTextField(
                    value = renameText,
                    onValueChange = { renameText = it },
                    label = { Text(stringResource(R.string.category_name)) },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (renameText.isNotBlank()) {
                        val newList = categories.toMutableList()
                        val idx = newList.indexOfFirst { it.id == renamingCategory!!.id }
                        if (idx != -1) {
                            newList[idx] = renamingCategory!!.copy(name = renameText.trim())
                            onUpdateCategories(newList)
                        }
                        showRenameDialog = false
                    }
                }) { Text(stringResource(R.string.confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSelectionScreen(
    category: AppCategory,
    allApps: List<AppItem>,
    onBack: () -> Unit,
    onSave: (AppCategory) -> Unit
) {
    val selectedPackages = remember { mutableStateListOf<String>().apply { addAll(category.packageNames) } }
    var searchQuery by remember { mutableStateOf("") }

    val displayedApps = remember(allApps, searchQuery) {
        if (searchQuery.isBlank()) allApps
        else allApps.filter {
            it.label.contains(searchQuery, ignoreCase = true) || it.packageName.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.edit_category_title, category.name)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                placeholder = { Text(stringResource(R.string.search_apps)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            LazyColumn {
                items(displayedApps) { app ->
                    val isSelected = selectedPackages.contains(app.packageName)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (isSelected) selectedPackages.remove(app.packageName)
                                else selectedPackages.add(app.packageName)
                                onSave(category.copy(packageNames = selectedPackages.toList()))
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppIcon(packageName = app.packageName, size = 40.dp)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = app.label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                        Switch(
                            checked = isSelected,
                            onCheckedChange = { checked ->
                                if (checked) selectedPackages.add(app.packageName)
                                else selectedPackages.remove(app.packageName)
                                onSave(category.copy(packageNames = selectedPackages.toList()))
                            }
                        )
                    }
                }
            }
        }
    }
}
