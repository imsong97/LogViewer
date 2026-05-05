package com.ch0pp4.logviewer.model


data class LoadResult(
    val fileMap: LinkedHashMap<String, List<String>>,
    val lines: List<LogLine>,
    val isFirstLoad: Boolean
)
