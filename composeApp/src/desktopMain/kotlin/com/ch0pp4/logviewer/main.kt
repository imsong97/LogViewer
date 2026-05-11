package com.ch0pp4.logviewer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.lifecycle.ViewModelStore
import com.ch0pp4.logviewer.resources.AppStrings
import com.ch0pp4.logviewer.ui.FileListLayout
import com.ch0pp4.logviewer.ui.LogContentLayout
import com.ch0pp4.logviewer.ui.LogFilterLayout
import com.ch0pp4.logviewer.utils.VMProvider
import com.logviewer.data.provider.LogFileProviderImpl

fun main() = application {
    val windowState = rememberWindowState(size = DpSize(width = 1400.dp, height = 900.dp))
    val viewModelStore = remember { ViewModelStore() }

    Window(
        onCloseRequest = {
            viewModelStore.clear()
            exitApplication()
        },
        title = AppStrings.APP_TITLE,
        state = windowState,
    ) {
        val logViewModel: LogViewModel = remember(key1 = viewModelStore) {
            VMProvider(
                store = viewModelStore,
                instance = LogViewModel(LogFileProviderImpl())
            )
        }

        val showOnlyBookmarks by logViewModel.showOnlyBookmarks.collectAsState()
        val activeTagFilters by logViewModel.activeTagFilters.collectAsState()
        val searchQuery by logViewModel.searchQuery.collectAsState()
        val searchEnabled by logViewModel.searchEnabled.collectAsState()
        val tagQuery by logViewModel.tagQuery.collectAsState()
        val tagSearchEnabled by logViewModel.tagSearchEnabled.collectAsState()
        val hideUnparsed by logViewModel.hideUnparsed.collectAsState()
        val bookmarkedLines by logViewModel.bookmarkedLines.collectAsState()
        val focusedLine by logViewModel.focusedLine.collectAsState()
        val selectedLines by logViewModel.selectedLines.collectAsState()
        val existFile by logViewModel.existFile.collectAsState()
        val displayLines by logViewModel.displayLines.collectAsState(initial = emptyList())
        val loadedFiles by logViewModel.loadedFiles.collectAsState()
        val isLoading by logViewModel.isLoading.collectAsState()
        val showUnsupportedDialog by logViewModel.showUnSupportedDialog.collectAsState()

        MaterialTheme {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    LogFilterLayout(
                        showOnlyBookmarks = showOnlyBookmarks,
                        activeTagFilters = activeTagFilters,
                        searchQuery = searchQuery,
                        searchEnabled = searchEnabled,
                        tagQuery = tagQuery,
                        tagSearchEnabled = tagSearchEnabled,
                        hideUnparsed = hideUnparsed,
                        onSearchQueryChange = { logViewModel.setSearchQuery(it) },
                        onToggleSearchEnabled = { logViewModel.toggleSearchEnabled() },
                        onTagQueryChange = { logViewModel.setTagQuery(it) },
                        onToggleTagSearchEnabled = { logViewModel.toggleTagSearchEnabled() },
                        onToggleBookmarks = { logViewModel.toggleShowOnlyBookmarks() },
                        onToggleLevelFilter = { level -> logViewModel.toggleLevelFilter(level) },
                        onClearLevelFilters = { logViewModel.clearLevelFilters() },
                        onToggleHideUnparsed = { logViewModel.toggleHideUnparsed() },
                    )

                    FileListLayout(
                        loadedFiles = loadedFiles,
                        showUnSupportedDialog = showUnsupportedDialog,
                        onLoadFiles = { files -> logViewModel.loadFiles(files) },
                        onRemoveFile = { fileName -> logViewModel.removeFile(fileName) },
                        onDismissUnsupportedDialog = { logViewModel.dismissUnsupportedDialog() },
                        clearAll = { logViewModel.clearAll() },
                    )

                    Divider()

                    LogContentLayout(
                        modifier = Modifier.weight(weight = 1f),
                        existFile = existFile,
                        displayLines = displayLines,
                        bookmarkedLines = bookmarkedLines,
                        focusedLine = focusedLine,
                        selectedLines = selectedLines,
                        onFileDropped = { files -> logViewModel.loadFiles(files) },
                        onToggleBookmark = { index -> logViewModel.toggleBookmark(index) },
                        onFocusLine = { index -> logViewModel.setFocusedLine(index) },
                        onCtrlClickLine = { index -> logViewModel.toggleSelectedLine(index) }
                    )
                }

                // loading layout
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f))
                            .pointerInput(key1 = Unit) {
                                awaitPointerEventScope {
                                    while (true) {
                                        awaitPointerEvent()
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
            }
        }
    }
}