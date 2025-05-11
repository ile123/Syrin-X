package com.ile.syrin_x.ui.screen

import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ile.syrin_x.utils.getSpotifyAuthorizationUrl
import com.ile.syrin_x.utils.getSoundCloudAuthorizationUrl
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.ile.syrin_x.R
import com.ile.syrin_x.utils.EnvLoader
import com.ile.syrin_x.viewModel.MusicSourceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicSourceScreen(
    musicSourceViewModel: MusicSourceViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val spotifyUserToken by musicSourceViewModel.spotifyUserToken.collectAsState()
    val soundCloudUserToken by musicSourceViewModel.soundCloudUserToken.collectAsState()

    LaunchedEffect(Unit) {
        musicSourceViewModel.getUserSpotifyToken()
        musicSourceViewModel.getUserSoundCloudToken()
    }

    data class CarouselItem(
        val id: Int,
        @DrawableRes val imageResId: Int,
        @StringRes val contentDescriptionResId: Int
    )

    val items = listOf(
        CarouselItem(0, R.drawable.background_image_spotify_login, R.string.spotify_login),
        CarouselItem(1, R.drawable.background_image_soundcloud_login, R.string.soundcloud_login)
    )

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
        HorizontalMultiBrowseCarousel(
            state = rememberCarouselState { items.size },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            preferredItemWidth = LocalWindowInfo.current.containerSize.width.dp,
            itemSpacing = 0.dp,
            contentPadding = PaddingValues(0.dp)
        ) { pageIndex ->
            val item = items[pageIndex]

            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = item.imageResId),
                    contentDescription = stringResource(item.contentDescriptionResId),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                when (item.id) {
                    0 -> SpotifyLoginButton(
                        enabled = spotifyUserToken == null,
                        onClick = {
                            val url = getSpotifyAuthorizationUrl(
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
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, url.toUri())
                                    .apply {
                                        addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    }
                            )
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp)
                    )

                    1 -> SoundCloudLoginButton(
                        enabled = soundCloudUserToken == null,
                        onClick = {
                            val url = getSoundCloudAuthorizationUrl(
                                EnvLoader.soundCloudClientId,
                                "syrinx://app",
                                listOf("non-expiring")
                            )
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, url.toUri())
                                    .apply {
                                        addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    }
                            )
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SpotifyLoginButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val containerColor = when (enabled) {
        true -> MaterialTheme.colorScheme.primary
        false -> MaterialTheme.colorScheme.outline
    }

    val contentColor = when (enabled) {
        true -> MaterialTheme.colorScheme.onPrimary
        false -> MaterialTheme.colorScheme.outlineVariant
    }

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
    ) {
        Text(
            "Login with Spotify",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun SoundCloudLoginButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val containerColor = when (enabled) {
        true -> MaterialTheme.colorScheme.primary
        false -> MaterialTheme.colorScheme.outline
    }

    val contentColor = when (enabled) {
        true -> MaterialTheme.colorScheme.onPrimary
        false -> MaterialTheme.colorScheme.outlineVariant
    }

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
    ) {
        Text(
            "Login with SoundCloud",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}