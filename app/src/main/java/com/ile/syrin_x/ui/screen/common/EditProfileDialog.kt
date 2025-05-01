package com.ile.syrin_x.ui.screen.common

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun EditProfileDialog(
    currentUsername: String,
    currentFullName: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var userName by remember { mutableStateOf(currentUsername) }
    var fullName by remember { mutableStateOf(currentFullName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        text = {
            Column {
                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = { Text("Username") }
                )
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") }
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(userName, fullName) }) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}