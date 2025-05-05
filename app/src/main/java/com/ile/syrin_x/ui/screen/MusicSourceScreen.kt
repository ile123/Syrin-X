package com.ile.syrin_x.ui.screen

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ile.syrin_x.utils.getSpotifyAuthorizationUrl
import com.ile.syrin_x.utils.getSoundCloudAuthorizationUrl
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.ile.syrin_x.R
import com.ile.syrin_x.utils.EnvLoader
import com.ile.syrin_x.viewModel.MusicSourceViewModel

@Composable
fun MusicSourceScreen(
    musicSourceViewModel: MusicSourceViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val spotifyUserToken by musicSourceViewModel.spotifyUserToken.collectAsState()
    val soundCloudUserToken by musicSourceViewModel.soundCloudUserToken.collectAsState()

    LaunchedEffect(Unit){
        musicSourceViewModel.getUserSpotifyToken()
        musicSourceViewModel.getUserSoundCloudToken()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Image(
            painter = painterResource(id = R.drawable.background_image_1),
            contentDescription = "Background image",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                val authorizationUrl = getSpotifyAuthorizationUrl(
                    EnvLoader.spotifyClientId,
                    "syrinx://app/spotify",
                    listOf(
                        "user-read-private",
                        "user-read-email",
                        "user-modify-playback-state",
                        "user-read-playback-state",
                        "streaming"
                    )
                )
                val intent = Intent(Intent.ACTION_VIEW, authorizationUrl.toUri()).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                context.startActivity(intent)
            },
                enabled = spotifyUserToken == null
            ) {
                Text("Login with Spotify")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                val authorizationUrl = getSoundCloudAuthorizationUrl(
                    EnvLoader.soundCloudClientId,
                    "syrinx://app",
                    listOf("non-expiring")
                )
                val intent = Intent(Intent.ACTION_VIEW, authorizationUrl.toUri()).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                context.startActivity(intent)
            },
                enabled = soundCloudUserToken == null
            ) {
                Text("Login with SoundCloud")
            }
        }
    }
    }