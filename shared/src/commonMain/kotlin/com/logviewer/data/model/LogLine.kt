package com.logviewer.data.model

data class LogLine(
    val index: Int,
    val text: String,
    val fileName: String = "",
    val date: String? = null,
    val time: String? = null,
    val logLv: String? = null,
    val thread: String? = null,
    val tag: String? = null,
    val description: String? = null
    // pid: 3, skip
) {
    companion object {
        private val logLineRegex = Regex("")
        fun parse(index: Int, text: String, fileName: String): LogLine = LogLine(
            index = index,
            text = text,
            fileName = fileName
        )
    }
}