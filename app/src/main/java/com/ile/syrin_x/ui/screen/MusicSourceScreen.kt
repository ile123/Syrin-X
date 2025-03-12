package com.ile.syrin_x.ui.screen

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ile.syrin_x.utils.getSpotifyAuthorizationUrl
import androidx.lifecycle.LiveData
import com.ile.syrin_x.utils.getSoundCloudAuthorizationUrl
import com.ile.syrin_x.viewModel.MusicSourceViewModel
import androidx.core.net.toUri
import com.ile.syrin_x.R
import com.ile.syrin_x.utils.extractAuthorizationCode

@Composable
fun MusicSourceScreen(
    navHostController: NavHostController,
    intentData: LiveData<Uri?>,
    viewModel: MusicSourceViewModel = hiltViewModel()
) {
    val spotifyToken by viewModel.spotifyToken.observeAsState()
    val soundCloudToken by viewModel.soundCloudToken.observeAsState()
    val context = LocalContext.current
    val uri by intentData.observeAsState()

    LaunchedEffect(uri) {
        uri?.let {
            when {
                it.toString().startsWith("syrinx://app/spotify") -> {
                    val authorizationCode = extractAuthorizationCode(it)
                    authorizationCode?.let { code ->
                        viewModel.loginSpotify(code, "syrinx://app/spotify")
                    }
                }
                it.toString().startsWith("syrinx://app/soundcloud") -> {
                    val authorizationCode = extractAuthorizationCode(it)
                    authorizationCode?.let { code ->
                        viewModel.loginSoundCloud(code, "syrinx://app/soundcloud")
                    }
                }
                else -> {
                    Log.d("MusicSourceScreen", "It should hit either Spotify or SoundCloud.")
                }
            }
        }
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
                    "your_spotify_client_id",
                    "syrinx://app/spotify",
                    listOf("user-read-private", "user-read-email")
                )
                val intent = Intent(Intent.ACTION_VIEW, authorizationUrl.toUri())
                context.startActivity(intent)
            }) {
                Text("Login with Spotify")
            }

            spotifyToken?.let {
                Text("Spotify Access Token: ${it.accessToken}")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                val authorizationUrl = getSoundCloudAuthorizationUrl(
                    "your_soundcloud_client_id",
                    "syrinx://app/soundcloud",
                    listOf("non-expiring")
                )
                val intent = Intent(Intent.ACTION_VIEW, authorizationUrl.toUri())
                context.startActivity(intent)
            }) {
                Text("Login with SoundCloud")
            }

            soundCloudToken?.let {
                Text("SoundCloud Access Token: ${it.accessToken}")
            }
        }
    }
    }