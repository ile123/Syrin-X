package com.ile.syrin_x.ui.screen.common

import android.util.Log
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
import androidx.compose.runtime.collectAsState
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
    title: String = "Add to Playlist",
    confirmButtonText: String = "Done",
    dismissButtonText: String = "Cancel",
    cancelable: Boolean = true,
    playlistManagementViewModel: PlaylistManagementViewModel = hiltViewModel()
) {
    val playlists by playlistManagementViewModel.userPlaylists.collectAsState(initial = emptyList())

    val initialSelectedIds = remember(
        playlists.map { it.userCreatedPlaylistId to it.tracks.map { t -> t.trackId }.toSet() },
        track.id
    ) {
        playlists
            .filter { pw -> pw.tracks.any { it.trackId == track.id } }
            .map { it.userCreatedPlaylistId }
            .toSet()
    }

    var selectedIds by remember(
        playlists.map { it.userCreatedPlaylistId },
        track.id
    ) {
        mutableStateOf(initialSelectedIds)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(Modifier.fillMaxWidth()) {
                if (playlists.isEmpty()) {
                    Text("You have no playlists yet.", Modifier.padding(16.dp))
                } else {
                    playlists.forEach { pw ->
                        val id = pw.userCreatedPlaylistId
                        val checked = selectedIds.contains(id)

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedIds =
                                        if (checked) selectedIds - id else selectedIds + id
                                }
                                .padding(vertical = 8.dp, horizontal = 16.dp)
                        ) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = { isNow ->
                                    selectedIds =
                                        if (isNow) selectedIds + id else selectedIds - id
                                }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(pw.name, Modifier.weight(1f))
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val toAdd = selectedIds - initialSelectedIds
                val toRemove = initialSelectedIds - selectedIds

                toAdd.forEach { pid ->
                    playlistManagementViewModel.addOrRemoveTrackFromPlaylist(
                        track, pid, UserCreatedPlaylistTrackAction.ADD_TRACK
                    )
                }
                toRemove.forEach { pid ->
                    playlistManagementViewModel.addOrRemoveTrackFromPlaylist(
                        track, pid, UserCreatedPlaylistTrackAction.REMOVE_TRACK
                    )
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