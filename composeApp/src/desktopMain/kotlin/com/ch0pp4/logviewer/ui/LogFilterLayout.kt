package com.ch0pp4.logviewer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ch0pp4.logviewer.resources.AppColors
import com.ch0pp4.logviewer.resources.AppStrings

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
    onSearchQueryChange: (String) -> Unit = {},
    onToggleSearchEnabled: () -> Unit = {},
    onTagQueryChange: (String) -> Unit = {},
    onToggleTagSearchEnabled: () -> Unit = {},
    onToggleBookmarks: () -> Unit = {},
    onToggleLevelFilter: (String) -> Unit = {},
    onClearLevelFilters: () -> Unit = {},
    onToggleHideUnparsed: () -> Unit = {},
) {
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
            Text(text = AppStrings.SEARCH_LABEL, modifier = Modifier.width(width = 70.dp))
            TextField(
                value = searchQuery,
                onValueChange = { onSearchQueryChange(it) },
                singleLine = true,
                modifier = Modifier.weight(weight = 1f).padding(end = 8.dp),
                placeholder = { Text(text = AppStrings.SEARCH_PLACEHOLDER) },
                colors = textFieldColors,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(size = 8.dp),
            )
            // text filter on/off toggle
            FilterChip(
                selected = searchEnabled,
                onClick = onToggleSearchEnabled,
                label = {
                    Text(
                        text = if (searchEnabled) AppStrings.SEARCH_FILTER_ON else AppStrings.SEARCH_FILTER_OFF,
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
            Text(text = AppStrings.SEARCH_TAG_LABEL, modifier = Modifier.width(width = 70.dp))
            TextField(
                value = tagQuery,
                onValueChange = { onTagQueryChange(it) },
                singleLine = true,
                modifier = Modifier.weight(weight = 1f).padding(end = 8.dp),
                placeholder = { Text(text = AppStrings.SEARCH_TAG_PLACEHOLDER) },
                colors = textFieldColors,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(size = 8.dp),
            )
            // tag filter on/off toggle
            FilterChip(
                selected = tagSearchEnabled,
                onClick = onToggleTagSearchEnabled,
                label = {
                    Text(
                        text = if (tagSearchEnabled) AppStrings.SEARCH_TAG_FILTER_ON else AppStrings.SEARCH_TAG_FILTER_OFF,
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
            Text(text = AppStrings.FILTER_LOG_LEVEL, fontSize = 13.sp)
            FilterChip(
                selected = AppStrings.COMMON_LOG_LEVEL_D in activeTagFilters,
                onClick = { onToggleLevelFilter(AppStrings.COMMON_LOG_LEVEL_D) },
                label = { Text(text = AppStrings.COMMON_LOG_LEVEL_D, fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AppColors.chipDebugBackground,
                    selectedLabelColor = AppColors.chipDebugText,
                )
            )

            FilterChip(
                selected = AppStrings.COMMON_LOG_LEVEL_W in activeTagFilters,
                onClick = { onToggleLevelFilter(AppStrings.COMMON_LOG_LEVEL_W) },
                label = { Text(text = AppStrings.COMMON_LOG_LEVEL_W, fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AppColors.chipWarningBackground,
                    selectedLabelColor = AppColors.chipWarningText,
                )
            )

            FilterChip(
                selected = AppStrings.COMMON_LOG_LEVEL_E in activeTagFilters,
                onClick = { onToggleLevelFilter(AppStrings.COMMON_LOG_LEVEL_E) },
                label = { Text(text = AppStrings.COMMON_LOG_LEVEL_E, fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AppColors.chipErrorBackground,
                    selectedLabelColor = AppColors.chipErrorText,
                )
            )

            if (activeTagFilters.isNotEmpty()) {
                TextButton(onClick = onClearLevelFilters) {
                    Text(text = AppStrings.FILTER_LOG_LEVEL_RESET, fontSize = 11.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.weight(weight = 1f))

            FilterChip(
                selected = showOnlyBookmarks,
                onClick = onToggleBookmarks,
                label = { Text(text = AppStrings.FILTER_BOOKMARK, fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AppColors.bookMarkBackground,
                    selectedLabelColor = AppColors.chipBookMarkText,
                )
            )

            FilterChip(
                selected = hideUnparsed,
                onClick = onToggleHideUnparsed,
                label = { Text(text = AppStrings.HIDE_UNPARSED_LOGS, fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AppColors.chipErrorBackground,
                    selectedLabelColor = AppColors.chipErrorText,
                )
            )
        }
    }
}