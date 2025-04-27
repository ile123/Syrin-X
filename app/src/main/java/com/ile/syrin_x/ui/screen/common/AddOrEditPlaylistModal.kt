package com.ile.syrin_x.ui.screen.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.ile.syrin_x.data.model.usercreatedplaylist.UserCreatedPlaylist
import com.ile.syrin_x.viewModel.PlaylistManagementViewModel

@Composable
fun AddOrEditPlaylistModal(
    playlist: UserCreatedPlaylist? = null,
    onDismiss: () -> Unit,
    confirmButtonText: String,
    dismissButtonText: String,
    cancelable: Boolean,
    playlistManagementViewModel: PlaylistManagementViewModel = hiltViewModel()
) {
    var playlistName by remember { mutableStateOf(playlist?.name.orEmpty()) }
    val isEdit = (playlist != null)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = if (isEdit) "Rename Playlist" else "Create Playlist")
        },
        text = {
            Column {
                Text("Playlist name")
                Spacer(Modifier.height(8.dp))
                TextField(
                    value = playlistName,
                    onValueChange = { playlistName = it },
                    label = { Text("Playlist name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (playlistName.isBlank()) return@TextButton
                    if (isEdit) {
                        playlistManagementViewModel.updateUserPlaylist(
                            playlistId = playlist!!.userCreatedPlaylistId,
                            newName = playlistName.trim()
                        )
                    } else {
                        playlistManagementViewModel.createPlaylist(playlistName.trim())
                    }
                    onDismiss()
                },
                enabled = playlistName.isNotBlank()
            ) {
                Text(confirmButtonText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissButtonText)
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = cancelable,
            dismissOnClickOutside = cancelable
        )
    )
}