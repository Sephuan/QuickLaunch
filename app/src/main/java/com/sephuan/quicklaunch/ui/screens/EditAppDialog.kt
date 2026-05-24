package com.sephuan.quicklaunch.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.sephuan.quicklaunch.App
import com.sephuan.quicklaunch.R
import com.sephuan.quicklaunch.data.AppCustomConfig
import com.sephuan.quicklaunch.data.AppItem

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditAppDialog(
    app: AppItem,
    currentConfig: AppCustomConfig,
    onDismiss: () -> Unit,
    onSave: (AppCustomConfig) -> Unit
) {
    val appCtx = LocalContext.current.applicationContext as App
    val categoryManager = appCtx.categoryManager

    var customName by remember { mutableStateOf(currentConfig.customName) }
    val tags = remember { mutableStateListOf<String>().apply { addAll(currentConfig.tags) } }
    var newTagInput by remember { mutableStateOf("") }
    var isPinned by remember { mutableStateOf(currentConfig.isPinned) }

    val allCategories = remember { categoryManager.getCategories() }
    val selectedCategoryIds = remember { mutableStateListOf<String>().apply { addAll(categoryManager.getCategoryIdsForApp(app.packageName)) } }

    fun addTag() {
        if (newTagInput.isNotBlank() && !tags.contains(newTagInput.trim())) {
            tags.add(newTagInput.trim())
            newTagInput = ""
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.edit_app_title, app.label)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(R.string.pin_to_home), modifier = Modifier.weight(1f))
                    Switch(checked = isPinned, onCheckedChange = { isPinned = it })
                }

                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.categories_label),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (allCategories.isEmpty()) {
                    Text(stringResource(R.string.no_categories_hint), style = MaterialTheme.typography.bodySmall)
                } else {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        allCategories.forEach { category ->
                            val isSelected = selectedCategoryIds.contains(category.id)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (isSelected) selectedCategoryIds.remove(category.id)
                                    else selectedCategoryIds.add(category.id)
                                },
                                label = { Text(category.name) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = customName,
                    onValueChange = { customName = it },
                    label = { Text(stringResource(R.string.custom_alias)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = stringResource(R.string.search_tags), style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(8.dp))

                if (tags.isEmpty()) {
                    Text(
                        stringResource(R.string.no_tags),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                } else {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tags.forEach { tag ->
                            InputChip(
                                selected = false,
                                onClick = { },
                                label = { Text(tag) },
                                trailingIcon = {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = stringResource(R.string.remove),
                                        modifier = Modifier.clickable { tags.remove(tag) }
                                    )
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newTagInput,
                        onValueChange = { newTagInput = it },
                        label = { Text(stringResource(R.string.add_tag)) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { addTag() })
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = { addTag() }) {
                        Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val newConfig = currentConfig.copy(
                    customName = customName.trim(),
                    tags = tags.toList(),
                    isPinned = isPinned
                )
                onSave(newConfig)
                categoryManager.updateAppCategories(app.packageName, selectedCategoryIds.toList())
            }) { Text(stringResource(R.string.save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}
