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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ch0pp4.logviewer.resources.AppColors
import logviewer.composeapp.generated.resources.Res
import logviewer.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import java.awt.FileDialog
import java.io.File
import java.io.FilenameFilter
import javax.swing.JFrame
import javax.swing.SwingUtilities

@Composable
fun FileListLayout(
    loadedFiles: List<String>,
    hiddenFiles: Set<String> = emptySet(),
    showUnSupportedDialog: Boolean = false,
    onLoadFiles: (List<File>) -> Unit,
    onRemoveFile: (String) -> Unit,
    onToggleFileVisibility: (String) -> Unit = {},
    onDismissUnsupportedDialog: () -> Unit = {},
    clearAll: () -> Unit
) {
    var showClearConfirm by remember { mutableStateOf(false) }

    val fileBrowserTitle = stringResource(Res.string.file_browser_title)

    if (showUnSupportedDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(text = stringResource(Res.string.file_browser_not_supported_title)) },
            text = { Text(text = stringResource(Res.string.common_unsupported_file_message)) },
            confirmButton = {
                Button(
                    onClick = { onDismissUnsupportedDialog() },
                    colors = buttonColors(containerColor = AppColors.deleteDialogButtonDeleteBackground)
                ) {
                    Text(text = stringResource(Res.string.common_dialog_btn_confirm), color = AppColors.deleteDialogButtonDelete)
                }
            },
            containerColor = Color.White
        )
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(text = stringResource(Res.string.file_remove_all), color = AppColors.textFieldText) },
            text = { Text(text = stringResource(Res.string.file_remove_dialog_message, loadedFiles.size), color = AppColors.textFieldText) },
            confirmButton = {
                Button(
                    onClick = {
                        clearAll()
                        showClearConfirm = false
                    },
                    colors = buttonColors(containerColor = AppColors.deleteDialogButtonDeleteBackground)
                ) {
                    Text(text = stringResource(Res.string.file_remove_dialog_btn_delete), color = AppColors.deleteDialogButtonDelete)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showClearConfirm = false },
                    border = BorderStroke(width = 1.dp, color = AppColors.deleteDialogButtonCancelBackground),
                ) {
                    Text(text = stringResource(Res.string.common_dialog_btn_cancel), color = AppColors.deleteDialogButtonCancel)
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

            val dialog = object : FileDialog(owner, fileBrowserTitle, FileDialog.LOAD) {
                override fun setVisible(value: Boolean) {
                    this.isMultipleMode = true
                    this.filenameFilter = FilenameFilter { _, name ->
                        name.endsWith(suffix = ".log") || name.endsWith(suffix = ".txt")
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
            Text(text = stringResource(Res.string.file_list_label), fontSize = 11.sp, color = Color.Gray)
            loadedFiles.forEach { fileName ->
                val isHidden = fileName in hiddenFiles
                Row(
                    modifier = Modifier
                        .alpha(if (isHidden) 0.4f else 1f)
                        .background(
                            color = AppColors.fileTagBackground,
                            shape = RoundedCornerShape(size = 4.dp)
                        )
                        .padding(start = 6.dp, end = 2.dp, top = 2.dp, bottom = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = fileName,
                        fontSize = 11.sp,
                        color = AppColors.fileTagText,
                        textDecoration = if (isHidden) TextDecoration.LineThrough else TextDecoration.None,
                        modifier = Modifier
                            .clickable { onToggleFileVisibility(fileName) }
                            .padding(end = 2.dp)
                    )
                    Text(
                        text = stringResource(Res.string.file_remove),
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
            Text(text = stringResource(Res.string.file_add_btn), fontSize = 11.sp, color = AppColors.fileTagText)
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
                text = stringResource(Res.string.file_remove_all),
                fontSize = 11.sp,
                color = if (loadedFiles.isNotEmpty()) AppColors.deleteButtonText else Color.Gray
            )
        }
    }
}
