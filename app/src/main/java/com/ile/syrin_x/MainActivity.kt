package com.ile.syrin_x

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.ile.syrin_x.service.TokenMonitorService
import com.ile.syrin_x.ui.navigation.SetUpNavigationGraph
import com.ile.syrin_x.ui.screen.player.PlayerScaffold
import com.ile.syrin_x.ui.theme.SyrinXTheme
import com.ile.syrin_x.viewModel.MusicSourceViewModel
import com.ile.syrin_x.viewModel.PlayerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val musicSourceViewModel: MusicSourceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: PlayerViewModel = hiltViewModel()
            SyrinXTheme {
                PlayerScaffold(viewModel = viewModel) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        SetUpNavigationGraph(viewModel)
                    }
                }
            }
        }
        handleDeepLink(intent?.data)
        startTokenMonitorService()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent.data)
    }

    private fun handleDeepLink(uri: Uri?) {
        uri?.let {
            when {
                uri.toString().startsWith("syrinx://app/spotify") -> {
                    val authCode = extractAuthorizationCode(uri)
                    if (!authCode.isNullOrEmpty()) {
                        musicSourceViewModel.loginSpotify(authCode, "syrinx://app/spotify")
                    }
                }
                uri.toString().startsWith("syrinx://app") -> {
                    val authCode = extractAuthorizationCode(uri)
                    if (!authCode.isNullOrEmpty()) {
                        musicSourceViewModel.loginSoundCloud(authCode, "syrinx://app")
                    }
                }
            }
        }
        Log.d("DeepLink", "Received URI: $uri")
    }

    private fun startTokenMonitorService() {
        val intent = Intent(this, TokenMonitorService::class.java)
        ContextCompat.startForegroundService(this, intent)
    }

    private fun extractAuthorizationCode(uri: Uri): String? {
        return uri.getQueryParameter("code")
    }
}
