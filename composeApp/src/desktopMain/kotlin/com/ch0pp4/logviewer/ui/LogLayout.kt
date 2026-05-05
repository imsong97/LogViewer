package com.ch0pp4.logviewer.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ch0pp4.logviewer.model.ColumnDef
import com.ch0pp4.logviewer.resources.AppColors
import com.ch0pp4.logviewer.resources.AppStrings

// layout
@Composable
fun LogLayout() {

}

@Composable
private fun LogRow() {

}

@Composable
private fun HeaderRow() {

}

// header columns
private val HEADER_COLUMNS = listOf(
    ColumnDef(AppStrings.HEADER_FILE, 160.dp),
    ColumnDef(AppStrings.HEADER_DATE, 72.dp),
    ColumnDef(AppStrings.HEADER_TIME, 104.dp),
    ColumnDef(AppStrings.HEADER_LOG_LEVEL, 48.dp),
    ColumnDef(AppStrings.HEADER_THREAD, 64.dp),
    ColumnDef(AppStrings.HEADER_TAG, 180.dp),
)

private fun logLVColor(level: String?): Color = when (level) {
    "E" -> AppColors.logLevelError
    "W" -> AppColors.logLevelWarning
    "D" -> AppColors.logLevelDebug
    else -> Color.Unspecified
}