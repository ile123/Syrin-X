package com.ile.syrin_x.ui.screen.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.ile.syrin_x.data.enums.MusicPlayerRepeatMode
import com.ile.syrin_x.data.model.UnifiedTrack
import com.ile.syrin_x.ui.icon.ExpandAllIcon
import com.ile.syrin_x.ui.icon.PauseIcon
import com.ile.syrin_x.ui.icon.PlayIcon
import com.ile.syrin_x.ui.icon.RepeatIcon
import com.ile.syrin_x.ui.icon.RepeatOnIcon
import com.ile.syrin_x.ui.icon.RepeatOnceOnIcon
import com.ile.syrin_x.ui.icon.SkipNextIcon
import com.ile.syrin_x.ui.icon.SkipPreviousIcon
import com.ile.syrin_x.utils.formatTime

@Composable
fun FullPlayerScreen(
    track: UnifiedTrack,
    isPlaying: Boolean,
    playbackPosition: Long,
    duration: Long,
    repeatMode: MusicPlayerRepeatMode,
    onBack: () -> Unit,
    onPlayPauseToggle: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onRepeatToggle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onBack) {
                Icon(ExpandAllIcon, contentDescription = "Expand All Icon")
            }
        }

        AsyncImage(
            model = track.artworkUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(track.title ?: "Unknown", color = Color.White, fontSize = 20.sp)
        Text(track.artists?.joinToString(", ") ?: "", color = Color.Gray, fontSize = 16.sp)

        Spacer(modifier = Modifier.height(8.dp))

        Slider(
            value = playbackPosition.toFloat(),
            onValueChange = { onSeekTo(it.toLong()) },
            valueRange = 0f..(duration.coerceAtLeast(1)).toFloat(),
            modifier = Modifier.fillMaxWidth()
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            Text(formatTime(playbackPosition), color = Color.LightGray, fontSize = 12.sp)
            Spacer(Modifier.weight(1f))
            Text(formatTime(duration), color = Color.LightGray, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = onPrevious) {
                Icon(SkipPreviousIcon, contentDescription = null, tint = Color.White)
            }
            IconButton(onClick = onPlayPauseToggle) {
                Icon(
                    imageVector = if (isPlaying) PauseIcon else PlayIcon,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = Color.White
                )
            }
            IconButton(onClick = onNext) {
                Icon(SkipNextIcon, contentDescription = null, tint = Color.White)
            }
        }

        IconButton(onClick = onRepeatToggle) {
            val icon = when (repeatMode) {
                MusicPlayerRepeatMode.OFF -> RepeatIcon
                MusicPlayerRepeatMode.ALL -> RepeatOnIcon
            }
            Icon(icon, contentDescription = null, tint = Color.White)
        }
    }
}
