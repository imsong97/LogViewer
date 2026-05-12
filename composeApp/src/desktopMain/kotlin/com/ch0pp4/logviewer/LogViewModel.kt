package com.ch0pp4.logviewer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ch0pp4.logviewer.model.LoadResult
import com.ch0pp4.logviewer.model.LogLine
import com.ch0pp4.logviewer.model.RemoveResult
import com.ch0pp4.logviewer.model.toPresentation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield
import kotlinx.coroutines.FlowPreview
import java.io.File
import com.logviewer.data.provider.LogFileProvider
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlin.collections.emptyList

@OptIn(FlowPreview::class)
class LogViewModel(
    private val logFileProvider: LogFileProvider
) : ViewModel() {
    // parsing-cache, change only when file is loaded or removed
    private val _parsedLines = MutableStateFlow<List<LogLine>>(emptyList())

    // for progress
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _existFile = MutableStateFlow(false)
    val existFile: StateFlow<Boolean> = _existFile.asStateFlow()

    private val _bookmarkedLines = MutableStateFlow<Set<Int>>(emptySet())
    val bookmarkedLines: StateFlow<Set<Int>> = _bookmarkedLines.asStateFlow()

    private val _focusedLine = MutableStateFlow<Int?>(null)
    val focusedLine: StateFlow<Int?> = _focusedLine.asStateFlow()

    // for Ctrl+Click
    private val _selectedLines = MutableStateFlow<Set<Int>>(emptySet())
    val selectedLines: StateFlow<Set<Int>> = _selectedLines.asStateFlow()

    // unsupported file dialog
    private val _showUnSupportedDialog = MutableStateFlow(false)
    val showUnSupportedDialog: StateFlow<Boolean> = _showUnSupportedDialog.asStateFlow()

    private val _showOnlyBookmarks = MutableStateFlow(false)
    val showOnlyBookmarks: StateFlow<Boolean> = _showOnlyBookmarks.asStateFlow()

    private val _hideUnparsed = MutableStateFlow(false)
    val hideUnparsed: StateFlow<Boolean> = _hideUnparsed.asStateFlow()

    // LogLV
    private val _activeTagFilters = MutableStateFlow<Set<String>>(emptySet())
    val activeTagFilters: StateFlow<Set<String>> = _activeTagFilters.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchEnabled = MutableStateFlow(true)
    val searchEnabled: StateFlow<Boolean> = _searchEnabled.asStateFlow()

    private val _tagQuery = MutableStateFlow("")
    val tagQuery: StateFlow<String> = _tagQuery.asStateFlow()

    private val _tagSearchEnabled = MutableStateFlow(true)
    val tagSearchEnabled: StateFlow<Boolean> = _tagSearchEnabled.asStateFlow()

    // for merging when file added/removed
    private val _fileLineMap = MutableStateFlow<LinkedHashMap<String, List<String>>>(LinkedHashMap())

    private val _loadedFiles = MutableStateFlow<List<String>>(emptyList())
    val loadedFiles: StateFlow<List<String>> = _loadedFiles.asStateFlow()

    // debounce query
    private val debouncedSearchQuery = _searchQuery.debounce(300L)
    private val debouncedTagQuery = _tagQuery.debounce(300L)

    // display log (main)
    val displayLines: Flow<List<LogLine>> =
        combine(
            combine(_parsedLines, _bookmarkedLines, _showOnlyBookmarks) { lines, bookmarks, onlyBookmarks -> Triple(lines, bookmarks, onlyBookmarks) },
            combine(_activeTagFilters, debouncedSearchQuery, _searchEnabled) { tagFilters, query, enabled -> Triple(tagFilters, query, enabled) },
            combine(debouncedTagQuery, _tagSearchEnabled, _hideUnparsed) { tagQuery, tagEnabled, hideUnparsed -> Triple(tagQuery, tagEnabled, hideUnparsed) }
        ) { (parsedLines, bookmarks, onlyBookmarks), (tagFilters, query, enabled), (tagQuery, tagEnabled, hideUnparsed) ->
            val keywords = if (enabled) query.split("|").map { it.trim() }.filter { it.isNotEmpty() } else emptyList()
            val tagKeywords = if (tagEnabled) tagQuery.split("|").map { it.trim() }.filter { it.isNotEmpty() } else emptyList()

            parsedLines.filter { logLine ->
                val index = logLine.index
                if (hideUnparsed && !logLine.isParsed) return@filter false
                val passBookmark = !onlyBookmarks || index in bookmarks
                val passLevel = tagFilters.isEmpty() || logLine.logLv in tagFilters
                val passSearch = keywords.isEmpty() || keywords.any { kw -> logLine.text.contains(kw, ignoreCase = true) }
                val passTagSearch = tagKeywords.isEmpty() || tagKeywords.any { kw -> (logLine.tag ?: "").contains(kw, ignoreCase = true) }

                passBookmark && passLevel && passSearch && passTagSearch
            }
        }.flowOn(Dispatchers.IO)

    fun loadFiles(files: List<File>) {
        val supportedFiles = files.filter { logFileProvider.isSupported(it) }

        if (supportedFiles.isEmpty()) {
            _showUnSupportedDialog.value = true
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            yield() // UI rendering first
            val startTime = System.currentTimeMillis()

            val result = withContext(Dispatchers.Default) {
                val currentMap = LinkedHashMap(_fileLineMap.value)
                val loadedEntries = coroutineScope {
                    supportedFiles.map { file ->
                        async {
                            file.name to logFileProvider.readLines(file)
                        }
                    }.awaitAll()
                }

                loadedEntries.forEach { (fileName, lines) ->
                    currentMap[fileName] = lines
                }

                val mergedLines = logFileProvider.mergeAndSort(currentMap)
                val parsed = logFileProvider.parseLines(mergedLines).map { it.toPresentation() }

                LoadResult(currentMap, parsed, !_existFile.value)
            }

            _fileLineMap.value = result.fileMap
            _loadedFiles.value = result.fileMap.keys.toList()
            _parsedLines.value = result.lines

            if (result.isFirstLoad) {
                _bookmarkedLines.value = emptySet()
                _focusedLine.value = null
            }
            _existFile.value = true

            // min 0.5s
            val elapsed = System.currentTimeMillis() - startTime
            if (elapsed < 500L) delay(500L - elapsed)
            _isLoading.value = false
        }
    }

    fun dismissUnsupportedDialog() {
        _showUnSupportedDialog.value = false
    }

    fun removeFile(fileName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            yield() // UI rendering first
            val startTime = System.currentTimeMillis()

            val currentMap = LinkedHashMap(_fileLineMap.value).apply {
                this.remove(fileName)
            }

            if (currentMap.isEmpty()) {
                clearAll()
            } else {
                val removeResult = withContext(Dispatchers.Default) {
                    val merged = logFileProvider.mergeAndSort(currentMap)
                    val parsed = logFileProvider.parseLines(merged).map { it.toPresentation() }

                    RemoveResult(currentMap, parsed)
                }

                _fileLineMap.value = removeResult.fileMap
                _loadedFiles.value = removeResult.fileMap.keys.toList()
                _parsedLines.value = removeResult.lines
                _bookmarkedLines.value = emptySet()
                _focusedLine.value = null
                _existFile.value = true
            }

            val elapsed = System.currentTimeMillis() - startTime
            if (elapsed < 500L) delay(500L - elapsed)
            _isLoading.value = false
        }
    }

    fun toggleBookmark(index: Int) {
        _bookmarkedLines.update { current ->
            if (index in current) current - index else current + index
        }
    }

    fun setFocusedLine(index: Int?) {
        _focusedLine.value = if (_focusedLine.value == index) null else index
        _selectedLines.value = emptySet() // clear selection when focusing a line (or unfocusing)
    }

    fun toggleSelectedLine(index: Int) {
        _selectedLines.update { current ->
            if (index in current) current - index else current + index
        }
        _focusedLine.value = index
    }

    fun toggleShowOnlyBookmarks() {
        _showOnlyBookmarks.update { !it }
    }

    fun toggleHideUnparsed() {
        _hideUnparsed.update { !it }
    }

    fun toggleLevelFilter(level: String) {
        _activeTagFilters.update { current ->
            if (level in current) current - level else current + level
        }
    }

    fun clearLevelFilters() {
        _activeTagFilters.value = emptySet()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleSearchEnabled() {
        _searchEnabled.update { !it }
    }

    fun setTagQuery(query: String) {
        _tagQuery.value = query
    }

    fun toggleTagSearchEnabled() {
        _tagSearchEnabled.update { !it }
    }

    fun clearAll() {
        _parsedLines.value = emptyList()
        _bookmarkedLines.value = emptySet()
        _focusedLine.value = null
        _selectedLines.value = emptySet()
        _existFile.value = false
        _showOnlyBookmarks.value = false
        _hideUnparsed.value = false
        _activeTagFilters.value = emptySet()
        _searchQuery.value = ""
        _searchEnabled.value = true
        _tagQuery.value = ""
        _tagSearchEnabled.value = true
        _loadedFiles.value = emptyList()
        _fileLineMap.value = LinkedHashMap()
    }
}