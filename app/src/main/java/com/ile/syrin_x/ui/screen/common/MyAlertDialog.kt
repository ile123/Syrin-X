package com.ile.syrin_x.ui.screen.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties

@Composable
fun MyAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    title: String,
    text: String,
    confirmButtonText: String,
    dismissButtonText: String,
    cancelable: Boolean
) {
    AlertDialog(
        title = { Text(text = title) },
        text = { Text(text = text) },
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            TextButton(
                onClick = { onConfirmation() }
            ) {
                Text(confirmButtonText)
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismissRequest() }
            ) {
                Text(dismissButtonText)
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = cancelable,
            dismissOnClickOutside = cancelable,
        )
    )
}