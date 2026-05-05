package com.logviewer.data

import com.logviewer.data.model.LineBlock
import com.logviewer.data.model.LogLine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class LogFileProviderImpl : LogFileProvider {

    private val timestampRegex = Regex("""^(\d{2}-\d{2})\s(\d{2}:\d{2}:\d{2}\.\d{3})""") // for sort with time "MM-DD HH:MM:SS.mm"
    private val supportExtension = listOf("txt", "log") // support file extensions

    override fun isSupported(file: File): Boolean = supportExtension.any { ext ->
        file.name.endsWith(".$ext") || file.name.contains(".$ext")
    }

    override suspend fun readLines(file: File): List<String> = withContext(Dispatchers.IO) {
        runCatching {
            file.useLines { it.toList() }
        }.getOrElse {
            emptyList()
        }
    }

    // sort with time
    override suspend fun mergeAndSort(
        fileMap: LinkedHashMap<String,
                List<String>>
    ): List<Pair<String, String>> = withContext(Dispatchers.Default) {
        val blocks = mutableListOf<LineBlock>()
        var currentBlock: LineBlock? = null

        fileMap.forEach { (fileName, lines) ->
            lines.forEach { l ->
                extractTimestamp(l)?.let { timestamp ->
                    LineBlock(timestamp, mutableListOf(fileName to l)).also {
                        blocks.add(it)
                        currentBlock = it
                    }
                } ?: run {
                    if (currentBlock != null) {
                        currentBlock?.lines?.add(fileName to l)
                    } else {
                        blocks.add(LineBlock("", mutableListOf(fileName to l)))
                    }
                }
            }
        }

        blocks.sortWith(compareBy { it.timeStamp })
        blocks.flatMap { it.lines }
    }

    // map to LogLine data class
    override suspend fun parseLines(
        mergedLines: List<Pair<String, String>>
    ): List<LogLine> = withContext(Dispatchers.Default) {
        mergedLines.mapIndexed { index, (fileName, line) ->
            LogLine.parse(index, line, fileName)
        }
    }

    private fun extractTimestamp(line: String): String? = timestampRegex.find(line)?.value
}