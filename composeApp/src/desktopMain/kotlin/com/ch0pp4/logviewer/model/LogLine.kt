package com.ch0pp4.logviewer.model

import com.logviewer.data.model.LogLine as LogLineModel

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
) {
    val isParsed: Boolean get() = date != null
}

fun LogLineModel.toPresentation() = LogLine(
    index = index,
    text = text,
    fileName = fileName,
    date = date,
    time = time,
    logLv = logLv,
    thread = thread,
    tag = tag,
    description = description
)
