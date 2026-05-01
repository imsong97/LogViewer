package com.ch0pp4.logviewer

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "LogViewer",
    ) {
        App()
    }
}