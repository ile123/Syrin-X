package com.ile.syrin_x.ui.screen.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.ile.syrin_x.data.enums.UserCreatedPlaylistTrackAction
import com.ile.syrin_x.data.model.UnifiedTrack
import com.ile.syrin_x.viewModel.PlaylistManagementViewModel

@Composable
fun AddTrackToPlaylistModal(
    track: UnifiedTrack,
    onDismiss: () -> Unit,
    title: String,
    confirmButtonText: String,
    dismissButtonText: String,
    cancelable: Boolean,
    playlistManagementViewModel: PlaylistManagementViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        playlistManagementViewModel.getAllUsersPlaylists()
    }

    val playlists = playlistManagementViewModel.userPlaylists

    val currentChecked = remember { mutableStateMapOf<String, Boolean>() }
    var initialCheckedMap by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }

    LaunchedEffect(track.id, playlists) {
        val init = playlists.associate { pw ->
            pw.userCreatedPlaylistId to pw.tracks.any { it.trackId == track.id }
        }
        initialCheckedMap = init
        currentChecked.clear()
        currentChecked.putAll(init)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (playlists.isEmpty()) {
                    Text("You have no playlists yet.", modifier = Modifier.padding(16.dp))
                }
                playlists.forEach { pw ->
                    val checked = currentChecked[pw.userCreatedPlaylistId] == true
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                currentChecked[pw.userCreatedPlaylistId] = !checked
                            }
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                    ) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { currentChecked[pw.userCreatedPlaylistId] = it }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(pw.name, modifier = Modifier.weight(1f))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                currentChecked.forEach { (playlistId, isNowChecked) ->
                    val wasChecked = initialCheckedMap[playlistId] == true
                    if (isNowChecked != wasChecked) {
                        val action = if (isNowChecked)
                            UserCreatedPlaylistTrackAction.ADD_TRACK
                        else
                            UserCreatedPlaylistTrackAction.REMOVE_TRACK

                        playlistManagementViewModel
                            .addOrRemoveTrackFromPlaylist(track, playlistId, action)
                    }
                }
                onDismiss()
            }) {
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