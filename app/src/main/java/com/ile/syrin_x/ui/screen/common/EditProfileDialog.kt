package com.ile.syrin_x.ui.screen.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

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
        title = { Text("Edit Profile",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold) },
        text = {
            Column {
                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = { Text("Username",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Normal
                    )}
                )
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    modifier = Modifier.padding(top = 16.dp),
                    label = { Text("Full Name",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Normal) }
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(userName, fullName) },
                colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),) {
                Text("Update",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    )
}