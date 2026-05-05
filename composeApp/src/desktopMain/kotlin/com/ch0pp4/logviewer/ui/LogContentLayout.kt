package com.ch0pp4.logviewer.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.defaultScrollbarStyle
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragData
import androidx.compose.ui.draganddrop.dragData
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isCtrlPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ch0pp4.logviewer.model.ColumnDef
import com.ch0pp4.logviewer.model.LogLine
import com.ch0pp4.logviewer.resources.AppColors
import com.ch0pp4.logviewer.resources.AppStrings
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File
import java.net.URI

// layout
@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun LogContentLayout(
    modifier: Modifier = Modifier,
    existFile: Boolean,
    displayLines: List<LogLine> = emptyList(),
    bookmarkedLines: Set<Int> = emptySet(),
    focusedLine: Int? = null,
    selectedLines: Set<Int> = emptySet(),
    onFileDropped: (List<File>) -> Unit = {},
    onToggleBookmark: (Int) -> Unit = {},
    onFocusLine: (Int) -> Unit = {},
    onCtrlClickLine: (Int) -> Unit = {}
) {
    var isDraggingOver by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val focusRequester = remember { FocusRequester() }

    // drag and drop listener
    val dragAndDropTarget = remember {
        object : DragAndDropTarget {
            override fun onStarted(event: DragAndDropEvent) {
                isDraggingOver = true
            }

            override fun onEnded(event: DragAndDropEvent) {
                isDraggingOver = false
            }
            override fun onDrop(event: DragAndDropEvent): Boolean {
                isDraggingOver = false
                return try {
                    val data = event.dragData()
                    if (data is DragData.FilesList) {
                        data.readFiles().mapNotNull { uri ->
                            runCatching {
                                File(URI(uri))
                            }.getOrNull()
                        }.also {
                            if (it.isEmpty()) return false
                            onFileDropped(it)
                        }
                        true
                    } else {
                        false
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
            .dragAndDropTarget(shouldStartDragAndDrop = { true }, target = dragAndDropTarget)
            .then(
                if (isDraggingOver) {
                    Modifier.border(2.dp, AppColors.dropBorderActive).background(AppColors.dropBorderActive)
                } else {
                    Modifier.border(1.dp, AppColors.dropBoxBorderIdle)
                }
            )
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { event ->
                if (
                    event.type == KeyEventType.KeyDown
                    && event.isCtrlPressed
                    && event.key == Key.C
                ) {
                    val allSelected = selectedLines + setOfNotNull(focusedLine)
                    val lines = displayLines.filter {
                        it.index in allSelected
                    }.sortedBy {
                        it.index
                    }.joinToString("\n") {
                        it.text
                    }

                    if (lines.isNotEmpty()) {
                        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
                        clipboard.setContents(StringSelection(lines), null)
                        return@onKeyEvent true
                    } else {
                        return@onKeyEvent false
                    }
                } else {
                    return@onKeyEvent false
                }
            }
    ) {
        if (!existFile) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = AppStrings.DROPBOX_PLACEHOLDER, style = MaterialTheme.typography.body1, color = Color.Gray)
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.fillMaxSize()
                        .padding(start = 8.dp, top = 4.dp, bottom = 4.dp)
                        .padding(end = 20.dp)
                ) {
                    // header
                    HeaderRow()

                    // list
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            count = displayLines.size,
                            key = { i -> displayLines[i].index }
                        ) { i ->
                            val logLine = displayLines[i]
                            LogRow(
                                logLine = logLine,
                                isBookmarked = logLine.index in bookmarkedLines,
                                isFocused = logLine.index == focusedLine,
                                isSelected = logLine.index in selectedLines,
                                onTap = {
                                    focusRequester.requestFocus()
                                    onFocusLine(logLine.index)
                                },
                                onCtrlTap = {
                                    focusRequester.requestFocus()
                                    onCtrlClickLine(logLine.index)
                                },
                                onDoubleTap = { onToggleBookmark(logLine.index) }
                            )
                        }
                    }
                }

                // calculate bookmark location in displayLines
                val bookmarkedPositions = displayLines.mapIndexedNotNull { position, line ->
                    if (line.index in bookmarkedLines) position else null
                }

                // scroll bar && bookmark marker
                Box(
                    modifier = Modifier.align(Alignment.CenterEnd)
                        .fillMaxHeight()
                        .width(12.dp)
                        .drawWithContent {
                            drawContent()
                            // scroll bar && bookmark marker
                            val totalLines = displayLines.size
                            if (totalLines <= 0) return@drawWithContent
                            bookmarkedPositions.forEach { pos ->
                                val ratio = pos.toFloat() / totalLines.toFloat()
                                val y = ratio * size.height
                                drawRect(
                                    color = AppColors.scrollBarBookMark,
                                    topLeft = Offset(0f, y - 2f),
                                    size = Size(size.width, 4f)
                                )
                            }
                        }
                ) {
                    VerticalScrollbar(
                        adapter = rememberScrollbarAdapter(listState),
                        modifier = Modifier.fillMaxHeight(),
                        style = defaultScrollbarStyle().copy(
                            unhoverColor = AppColors.scrollBarUnhover,
                            hoverColor = AppColors.scrollBarHover
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun LogRow(
    logLine: LogLine,
    isBookmarked: Boolean,
    isFocused: Boolean,
    isSelected: Boolean,
    onTap: () -> Unit,
    onCtrlTap: () -> Unit,
    onDoubleTap: () -> Unit
) {
    val textColor = logLVColor(logLine.logLv)
    val hasBorder = isFocused || isSelected
    val bgColor = when {
        isBookmarked -> AppColors.bookMarkBackground
        else -> Color.Transparent
    }

    Row(
        modifier = Modifier.fillMaxWidth()
            .background(bgColor)
            .then(if (hasBorder) Modifier.border(1.dp, AppColors.lineFocusBorder) else Modifier)
            .pointerInput(logLine.index) {
                detectTapGestures(
                    onTap = { onTap() },
                    onDoubleTap = { onDoubleTap() }
                )
            }
            .pointerInput(logLine.index) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        if (event.type == PointerEventType.Press && event.keyboardModifiers.isCtrlPressed) {
                            event.changes.forEach {
                                it.consume()
                            }
                            onCtrlTap()
                        }
                    }
                }
            }
            .padding(vertical = 1.dp)
    ) {
        if (logLine.isParsed) {
            val fixedCells = listOf(
                logLine.fileName,
                logLine.date ?: "",
                logLine.time ?: "",
                logLine.logLv ?: "",
                logLine.thread ?: "",
                logLine.tag ?: ""
            )

            fixedCells.forEachIndexed { i, cell ->
                Text(
                    text = cell,
                    modifier = Modifier.width(HEADER_COLUMNS[i].width).padding(horizontal = 4.dp),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = if (i == 0) Color.Unspecified else textColor,
                    maxLines = 1
                )
            }
            // description
            Text(
                text = logLine.description ?: "",
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = textColor,
                maxLines = 1
            )
        } else {
            // if it can not parse, fill description with raw text
            Text(
                text = logLine.fileName,
                modifier = Modifier.width(HEADER_COLUMNS[0].width).padding(horizontal = 4.dp),
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                maxLines = 1
            )
            HEADER_COLUMNS.drop(1).forEach { col ->
                Spacer(modifier = Modifier.width(col.width))
            }
            Text(
                text = logLine.text,
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun HeaderRow() {
    Row(
        modifier = Modifier.fillMaxWidth()
            .background(AppColors.logHeaderBackground)
            .padding(vertical = 4.dp)
    ) {
        // header
        HEADER_COLUMNS.forEach { col ->
            Text(
                text = col.title,
                modifier = Modifier.width(col.width).padding(horizontal = 4.dp),
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.logHeaderText,
                maxLines = 1
            )
        }
        // header - description
        Text(
            text = AppStrings.HEADER_DESCRIPTION,
            modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.logHeaderText,
            maxLines = 1
        )
    }
    Divider(color = AppColors.logHeaderDivider, thickness = 1.dp)
}

// header columns
private val HEADER_COLUMNS = listOf(
    ColumnDef(AppStrings.HEADER_FILE, 160.dp),
    ColumnDef(AppStrings.HEADER_DATE, 72.dp),
    ColumnDef(AppStrings.HEADER_TIME, 104.dp),
    ColumnDef(AppStrings.HEADER_LOG_LEVEL, 48.dp),
    ColumnDef(AppStrings.HEADER_THREAD, 64.dp),
    ColumnDef(AppStrings.HEADER_TAG, 180.dp),
)

private fun logLVColor(level: String?): Color = when (level) {
    "E" -> AppColors.logLevelError
    "W" -> AppColors.logLevelWarning
    "D" -> AppColors.logLevelDebug
    else -> Color.Unspecified
}