package com.ch0pp4.logviewer.model

data class RemoveResult(
    val fileMap: LinkedHashMap<String, List<String>>,
    val lines: List<LogLine>
)
