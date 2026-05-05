package com.logviewer.data.model

data class LineBlock(
    val timeStamp: String,
    val lines: MutableList<Pair<String, String>> // (fileName, text)
)
