package com.ch0pp4.logviewer.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ch0pp4.logviewer.resources.AppColors
import com.ch0pp4.logviewer.resources.AppStrings
import java.awt.FileDialog
import java.io.File
import java.io.FilenameFilter
import javax.swing.JFrame
import javax.swing.SwingUtilities

@Composable
fun FileListLayout(
    loadedFiles: List<String>,
    showUnSupportedDialog: Boolean = false,
    onLoadFiles: (List<File>) -> Unit,
    onRemoveFile: (String) -> Unit,
    onDismissUnsupportedDialog: () -> Unit = {},
    clearAll: () -> Unit
) {
    var showClearConfirm by remember { mutableStateOf(false) }

    if (showUnSupportedDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = {},
            text = {},
            confirmButton = {
                Button(
                    onClick = { onDismissUnsupportedDialog() },
                    colors = buttonColors(containerColor = AppColors.deleteDialogButtonDeleteBackground)
                ) {
                    Text(text = AppStrings.COMMON_DIALOG_BTN_CONFIRM, color = AppColors.deleteDialogButtonDelete)
                }
            },
            containerColor = Color.White
        )
//        JOptionPane.showMessageDialog(
//            null,
//            AppStrings.COMMON_UNSUPPORTED_FILE_MESSAGE,
//            AppStrings.FILE_BROWSER_NOT_SUPPORTED_TITLE,
//            JOptionPane.WARNING_MESSAGE
//        )
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(text = AppStrings.FILE_REMOVE_ALL, color = AppColors.textFieldText) },
            text = { Text(text = AppStrings.FILE_REMOVE_DIALOG_MESSAGE.format(loadedFiles.size), color = AppColors.textFieldText) },
            confirmButton = {
                Button(
                    onClick = {
                        clearAll()
                        showClearConfirm = false
                    },
                    colors = buttonColors(containerColor = AppColors.deleteDialogButtonDeleteBackground)
                ) {
                    Text(text = AppStrings.FILE_REMOVE_DIALOG_BTN_DELETE, color = AppColors.deleteDialogButtonDelete)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showClearConfirm = false },
                    border = BorderStroke(width = 1.dp, color = AppColors.deleteDialogButtonCancelBackground),
                ) {
                    Text(text = AppStrings.COMMON_DIALOG_BTN_CANCEL, color = AppColors.deleteDialogButtonCancel)
                }
            },
            containerColor = Color.White,
        )
    }

    fun openFileDialog() {
        SwingUtilities.invokeLater {
            val owner = JFrame().apply {
                this.isUndecorated = true
                this.isVisible = true
                this.toFront()
            }

            val dialog = object : FileDialog(owner, AppStrings.FILE_BROWSER_TITLE, FileDialog.LOAD) {
                override fun setVisible(value: Boolean) {
                    this.isMultipleMode = true
                    this.filenameFilter = FilenameFilter { _, name ->
                        name.endsWith(suffix = AppStrings.COMMON_FILE_FORMAT_LOG) ||
                                name.endsWith(suffix = AppStrings.COMMON_FILE_FORMAT_TXT)
                    }
                    super.setVisible(value)
                }
            }

            dialog.isVisible = true

            val selected = dialog.files?.toList() ?: emptyList()

            dialog.dispose()
            owner.dispose()

            if (selected.isNotEmpty()) {
                onLoadFiles(selected)
            }
        }
    }

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
            onClick = { openFileDialog() },
            modifier = Modifier.padding(all = 0.dp)
        ) {
            Text(text = AppStrings.FILE_ADD_BTN, fontSize = 11.sp, color = AppColors.fileTagText)
        }

        // delete btn
        TextButton(
            onClick = {
                if (loadedFiles.size >= 2) {
                    showClearConfirm = true
                } else {
                    clearAll()
                }
            },
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