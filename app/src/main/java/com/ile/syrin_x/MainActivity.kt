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
import androidx.lifecycle.MutableLiveData
import com.ile.syrin_x.service.TokenMonitorService
import com.ile.syrin_x.ui.navigation.SetUpNavigationGraph
import com.ile.syrin_x.ui.theme.SyrinXTheme
import com.ile.syrin_x.utils.extractAuthorizationCode
import com.ile.syrin_x.viewModel.MusicSourceViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val _intentData = MutableLiveData<Uri?>(null)
    private val musicSourceViewModel: MusicSourceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SyrinXTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SetUpNavigationGraph()
                }
            }
        }
        handleDeepLink(intent?.data)
        startTokenMonitorService()
    }

    private fun handleDeepLink(uri: Uri?) {
        uri?.let {
            when {
                uri.toString().startsWith("syrinx://app/spotify") -> {
                    val authorizationCode: String? = extractAuthorizationCode(uri)
                    if (!authorizationCode.isNullOrEmpty()) {
                        musicSourceViewModel.loginSpotify(authorizationCode, "syrinx://app/spotify")
                    }
                }
                uri.toString().startsWith("syrinx://app") -> {
                    val authorizationCode = extractAuthorizationCode(uri)
                    if (!authorizationCode.isNullOrEmpty()) {
                        musicSourceViewModel.loginSoundCloud(authorizationCode, "syrinx://app")
                    }
                }
            }
        }
        Log.d("DeepLink", "Received URI: $uri")
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        _intentData.value = intent.data
        handleDeepLink(intent.data)
    }

    private fun startTokenMonitorService() {
        val intent = Intent(this, TokenMonitorService::class.java)
        ContextCompat.startForegroundService(this, intent)
    }

}

