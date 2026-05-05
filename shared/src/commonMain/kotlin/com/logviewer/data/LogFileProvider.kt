package com.logviewer.data

import com.logviewer.data.model.LogLine
import java.io.File

interface LogFileProvider {

    fun isSupported(file: File): Boolean
    suspend fun readLines(file: File): List<String>
    suspend fun mergeAndSort(fileMap: LinkedHashMap<String, List<String>>): List<Pair<String, String>>
    suspend fun parseLines(mergedLines: List<Pair<String, String>>): List<LogLine>
}