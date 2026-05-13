package com.logviewer.data.model

data class ParseResult(
    val lines: List<LogLine>,
    val buildInfo: BuildInfo
)
