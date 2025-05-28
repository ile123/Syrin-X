package com.ile.syrin_x.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ile.syrin_x.ui.icon.ChartDataIcon
import com.ile.syrin_x.ui.icon.CollectionIcon
import com.ile.syrin_x.ui.icon.MusicCastIcon
import com.ile.syrin_x.viewModel.OnboardingViewModel

data class OnboardingSection(
    val icon: ImageVector,
    val title: String,
    val description: String
)

private val sections = listOf(
    OnboardingSection(
        icon = MusicCastIcon,
        title = "Play from Multiple Sources",
        description = "Stream from Spotify, SoundCloud and more."
    ),
    OnboardingSection(
        icon = CollectionIcon,
        title = "Build Your Own Playlists",
        description = "Organize tracks into custom playlists in seconds."
    ),
    OnboardingSection(
        icon = ChartDataIcon,
        title = "Charts & New-Release Alerts",
        description = "Discover top hits and get pinged on fresh drops."
    )
)

@Composable
fun OnboardingScreen(
    navHostController: NavHostController,
    viewModel: OnboardingViewModel = hiltViewModel(),
    onFinished: () -> Unit
) {
    val hasSeen by viewModel.hasSeenOnboarding.collectAsState()

    if (hasSeen) {
        LaunchedEffect(Unit) { onFinished() }
        return
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            sections.forEach { section ->
                OnboardingSectionCard(section)
                Spacer(modifier = Modifier.height(24.dp))
            }

            Button(
                onClick = {
                    viewModel.markOnboardingShown()
                    onFinished()
                }
            ) {
                Text(text = "Get Started")
            }
        }
    }
}

@Composable
private fun OnboardingSectionCard(section: OnboardingSection) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = section.icon,
                contentDescription = section.title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(64.dp)
                    .padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = section.title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = section.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}