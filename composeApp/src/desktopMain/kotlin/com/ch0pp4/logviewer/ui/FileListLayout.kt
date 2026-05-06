package com.ch0pp4.logviewer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ch0pp4.logviewer.resources.AppColors
import com.ch0pp4.logviewer.resources.AppStrings
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
fun FileListLayout(
    loadedFiles: List<String>,
    onLoadFiles: (List<File>) -> Unit,
    onRemoveFile: (String) -> Unit,
    clearAll: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = AppColors.fileTagLayoutBackground)
            .padding(horizontal = 8.dp, vertical = 1.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(state = rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(space = 6.dp)
        ) {
            Text(text = AppStrings.FILE_LIST_LABEL, fontSize = 11.sp, color = Color.Gray)
            loadedFiles.forEach { fileName ->
                Row(
                    modifier = Modifier
                        .background(
                            color = AppColors.fileTagBackground,
                            shape = RoundedCornerShape(size = 4.dp)
                        )
                        .padding(start = 6.dp, end = 2.dp, top = 2.dp, bottom = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = fileName, fontSize = 11.sp, color = AppColors.fileTagText)
                    Text(
                        text = AppStrings.FILE_REMOVE,
                        fontSize = 11.sp,
                        color = AppColors.fileTagText,
                        modifier = Modifier
                            .clickable { onRemoveFile(fileName) }
                            .padding(horizontal = 4.dp)
                    )
                }
            }
        }

        // add btn
        TextButton(
            onClick = {
                JFileChooser().apply {
                    isMultiSelectionEnabled = true
                    fileSelectionMode = JFileChooser.FILES_ONLY
                    addChoosableFileFilter(
                        FileNameExtensionFilter("Files", "log", "txt")
                    )
                }.also { chooser ->
                    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                        val selectedFiles = chooser.selectedFiles.toList()
                        if (selectedFiles.isNotEmpty()) {
                            onLoadFiles(selectedFiles)
                        }
                    }
                }
            },
            modifier = Modifier.padding(all = 0.dp)
        ) {
            Text(text = AppStrings.FILE_ADD_BTN, fontSize = 11.sp, color = AppColors.fileTagText)
        }

        // delete btn
        TextButton(
            onClick = { clearAll() },
            enabled = loadedFiles.isNotEmpty(),
            modifier = Modifier.padding(all = 0.dp)
        ) {
            Text(
                text = AppStrings.FILE_REMOVE_ALL,
                fontSize = 11.sp,
                color = if (loadedFiles.isNotEmpty()) AppColors.deleteButtonText else Color.Gray
            )
        }
    }
}