package com.ch0pp4.logviewer.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ch0pp4.logviewer.resources.AppColors

@Composable
fun DeleteDialog(
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    dismissText: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = title, color = AppColors.textFieldText) },
        text = { Text(text = message, color = AppColors.textFieldText) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = buttonColors(containerColor = AppColors.deleteDialogButtonDeleteBackground)
            ) {
                Text(text = confirmText, color = AppColors.deleteDialogButtonDelete)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                border = BorderStroke(width = 1.dp, color = AppColors.deleteDialogButtonCancelBackground),
            ) {
                Text(text = dismissText, color = AppColors.deleteDialogButtonCancel)
            }
        },
        containerColor = Color.White,
    )
}
