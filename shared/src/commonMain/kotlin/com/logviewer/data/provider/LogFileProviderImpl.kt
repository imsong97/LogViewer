package com.logviewer.data.provider

import com.logviewer.data.model.BuildInfo
import com.logviewer.data.model.LineBlock
import com.logviewer.data.model.LogLine
import com.logviewer.data.model.ParseResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class LogFileProviderImpl : LogFileProvider {

    private val timestampRegex = Regex("""^(\d{2}-\d{2})\s(\d{2}:\d{2}:\d{2}\.\d{3})""") // for sort with time "MM-DD HH:MM:SS.mm"
    private val PATTERN_BUILD_BRANCH = ""
    private val PATTERN_STEP = ""
    private val PATTERN_SW_VERSION = ""
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

    // map to LogLine data class + extract build info
    override suspend fun parseLines(
        mergedLines: List<Pair<String, String>>
    ): ParseResult = withContext(Dispatchers.Default) {
        var buildBranch = ""
        var step = ""
        var swVersion = ""

        val parsed = mergedLines.mapIndexed { index, (fileName, line) ->
            if (fileName.startsWith("dump", ignoreCase = true)) {
                // branch
                if (buildBranch.isEmpty()) {
                    val start = line.indexOf(PATTERN_BUILD_BRANCH)
                    if (start >= 0) {
                        val valueStart = start + PATTERN_STEP.length
                        val valueEnd = line.indexOf("]", valueStart)
                        if (valueEnd > valueStart) {
                            buildBranch = line.substring(valueStart, valueEnd).trim()
                        }
                    }
                }
                // sw version
                if (swVersion.isEmpty()) {
                    val start = line.indexOf(PATTERN_SW_VERSION)
                    if (start >= 0) {
                        val valueStart = start + PATTERN_SW_VERSION.length
                        val valueEnd = line.indexOf("]", valueStart)
                        if (valueEnd > valueStart) {
                            swVersion = line.substring(valueStart, valueEnd).trim()
                        }
                    }
                }
                // step
                if (step.isEmpty()) {
                    val idx = line.indexOf(PATTERN_STEP)
                    if (idx >= 0) {
                        step = line.substring(idx + PATTERN_STEP.length)
                            .trimStart()
                            .takeWhile { it.isDigit() }
                    }
                }
            }

            LogLine.parse(index, line, fileName)
        }

        ParseResult(
            lines = parsed,
            buildInfo = BuildInfo(
                branch = buildBranch,
                step = step,
                swVersion = swVersion
            )
        )
    }

    private fun extractTimestamp(line: String): String? = timestampRegex.find(line)?.value
}