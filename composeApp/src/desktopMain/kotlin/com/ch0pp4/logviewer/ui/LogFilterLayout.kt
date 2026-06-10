package com.ch0pp4.logviewer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ch0pp4.logviewer.resources.AppColors
import logviewer.composeapp.generated.resources.Res
import logviewer.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun LogFilterLayout(
    modifier: Modifier = Modifier,
    showOnlyBookmarks: Boolean = false,
    activeTagFilters: Set<String> = emptySet(),
    searchQuery: String = "",
    searchEnabled: Boolean = true,
    tagQuery: String = "",
    tagSearchEnabled: Boolean = true,
    hideUnparsed: Boolean = false,
    availableTags: List<String> = emptyList(),
    onSearchQueryChange: (String) -> Unit = {},
    onToggleSearchEnabled: () -> Unit = {},
    onTagQueryChange: (String) -> Unit = {},
    onToggleTagSearchEnabled: () -> Unit = {},
    onToggleBookmarks: () -> Unit = {},
    onToggleLevelFilter: (String) -> Unit = {},
    onClearLevelFilters: () -> Unit = {},
    onToggleHideUnparsed: () -> Unit = {},
) {
    var tagDropdownExpanded by remember { mutableStateOf(false) }

    val levelD = stringResource(Res.string.common_log_level_d)
    val levelW = stringResource(Res.string.common_log_level_w)
    val levelE = stringResource(Res.string.common_log_level_e)

    val textFieldColors = TextFieldDefaults.colors(
        unfocusedContainerColor = AppColors.textFieldBackground,
        focusedContainerColor = AppColors.textFieldFocusBackground,
        unfocusedIndicatorColor = AppColors.textFieldIndicator,
        focusedIndicatorColor = AppColors.textFieldIndicator,
        unfocusedTextColor = AppColors.textFieldText,
        focusedTextColor = AppColors.textFieldText,
        unfocusedPlaceholderColor = AppColors.textFieldPlaceholder,
        focusedPlaceholderColor = AppColors.textFieldPlaceholder,
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(all = 8.dp)
    ) {
        // Text Filter layout
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(Res.string.search_label), modifier = Modifier.width(width = 70.dp))
            TextField(
                value = searchQuery,
                onValueChange = { onSearchQueryChange(it) },
                singleLine = true,
                modifier = Modifier.weight(weight = 1f).padding(end = 8.dp),
                placeholder = { Text(text = stringResource(Res.string.search_placeholder)) },
                colors = textFieldColors,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(size = 8.dp),
            )
            // text filter on/off toggle
            FilterChip(
                selected = searchEnabled,
                onClick = onToggleSearchEnabled,
                label = {
                    Text(
                        text = if (searchEnabled) stringResource(Res.string.search_filter_on) else stringResource(Res.string.search_filter_off),
                        fontSize = 12.sp
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AppColors.chipDebugBackground,
                    selectedLabelColor = AppColors.chipDebugText,
                )
            )
        }

        Spacer(modifier = Modifier.height(height = 6.dp))

        // Tag filter Layout
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(Res.string.search_tag_label), modifier = Modifier.width(width = 70.dp))
            Box(modifier = Modifier.weight(weight = 1f).padding(end = 8.dp)) {
                TextField(
                    value = tagQuery,
                    onValueChange = { onTagQueryChange(it) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(text = stringResource(Res.string.search_tag_placeholder)) },
                    colors = textFieldColors,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(size = 8.dp),
                    trailingIcon = {
                        IconButton(
                            onClick = { tagDropdownExpanded = !tagDropdownExpanded },
                            enabled = availableTags.isNotEmpty()
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = stringResource(Res.string.tag_list_description),
                                tint = AppColors.textFieldText,
                            )
                        }
                    }
                )

                val lastKeyword = tagQuery.split("|").last().trim()
                val filteredTags = availableTags.filter {
                    lastKeyword.isEmpty() || it.contains(lastKeyword, ignoreCase = true)
                }

                DropdownMenu(
                    expanded = tagDropdownExpanded && filteredTags.isNotEmpty(),
                    onDismissRequest = { tagDropdownExpanded = false },
                ) {
                    LazyColumn(modifier = Modifier.heightIn(max = 144.dp)) {
                        items(filteredTags) { tag ->
                            DropdownMenuItem(
                                text = { Text(text = tag, fontSize = 12.sp, color = AppColors.textFieldText) },
                                onClick = {
                                    val parts = tagQuery.split("|").map { it.trim() }
                                    val newQuery = (parts.dropLast(1) + tag)
                                        .filter { it.isNotEmpty() }
                                        .joinToString(" | ")
                                    onTagQueryChange(newQuery)
                                    tagDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            // tag filter on/off toggle
            FilterChip(
                selected = tagSearchEnabled,
                onClick = onToggleTagSearchEnabled,
                label = {
                    Text(
                        text = if (tagSearchEnabled) stringResource(Res.string.search_tag_filter_on) else stringResource(Res.string.search_tag_filter_off),
                        fontSize = 12.sp
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AppColors.chipDebugBackground,
                    selectedLabelColor = AppColors.chipDebugText,
                )
            )
        }

        // LogLV Filter Layout
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(space = 6.dp)
        ) {
            Text(text = stringResource(Res.string.filter_log_level), fontSize = 13.sp)
            FilterChip(
                selected = levelD in activeTagFilters,
                onClick = { onToggleLevelFilter(levelD) },
                label = { Text(text = levelD, fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AppColors.chipDebugBackground,
                    selectedLabelColor = AppColors.chipDebugText,
                )
            )

            FilterChip(
                selected = levelW in activeTagFilters,
                onClick = { onToggleLevelFilter(levelW) },
                label = { Text(text = levelW, fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AppColors.chipWarningBackground,
                    selectedLabelColor = AppColors.chipWarningText,
                )
            )

            FilterChip(
                selected = levelE in activeTagFilters,
                onClick = { onToggleLevelFilter(levelE) },
                label = { Text(text = levelE, fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AppColors.chipErrorBackground,
                    selectedLabelColor = AppColors.chipErrorText,
                )
            )

            if (activeTagFilters.isNotEmpty()) {
                TextButton(onClick = onClearLevelFilters) {
                    Text(text = stringResource(Res.string.filter_log_level_reset), fontSize = 11.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.weight(weight = 1f))

            FilterChip(
                selected = showOnlyBookmarks,
                onClick = onToggleBookmarks,
                label = { Text(text = stringResource(Res.string.filter_bookmark), fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AppColors.chipBookMarkBackground,
                    selectedLabelColor = AppColors.chipBookMarkText,
                )
            )

            FilterChip(
                selected = hideUnparsed,
                onClick = onToggleHideUnparsed,
                label = { Text(text = stringResource(Res.string.hide_unparsed_logs), fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AppColors.chipErrorBackground,
                    selectedLabelColor = AppColors.chipErrorText,
                )
            )
        }
    }
}
