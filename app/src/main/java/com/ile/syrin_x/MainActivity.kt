package com.ile.syrin_x

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ile.syrin_x.domain.repository.DataStoreRepository
import com.ile.syrin_x.service.TokenMonitorService
import com.ile.syrin_x.ui.navigation.SetUpNavigationGraph
import com.ile.syrin_x.ui.screen.player.PlayerScaffold
import com.ile.syrin_x.ui.theme.AppTheme
import com.ile.syrin_x.ui.theme.SyrinXTheme
import com.ile.syrin_x.utils.StripeInitializer
import com.ile.syrin_x.utils.extractAuthorizationCode
import com.ile.syrin_x.viewModel.MusicSourceViewModel
import com.ile.syrin_x.viewModel.PaymentViewModel
import com.ile.syrin_x.viewModel.PlayerViewModel
import com.stripe.android.ApiResultCallback
import com.stripe.android.PaymentConfiguration
import com.stripe.android.PaymentIntentResult
import com.stripe.android.Stripe
import com.stripe.android.model.StripeIntent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val musicSourceViewModel: MusicSourceViewModel by viewModels()
    private val paymentViewModel: PaymentViewModel by viewModels()

    @Inject
    lateinit var datastoreRepository: DataStoreRepository

    @Inject
    lateinit var stripeInitializer: StripeInitializer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val defaultTheme = AppTheme.SystemDefault
            val appTheme by produceState(initialValue = defaultTheme, key1 = datastoreRepository) {
                val storedName = datastoreRepository.getString("app_theme")
                value = storedName
                    ?.let { name -> AppTheme.values().find { it.name == name } }
                    ?: defaultTheme
            }

            val viewModel: PlayerViewModel = hiltViewModel()
            SyrinXTheme(appTheme = appTheme) {
                PlayerScaffold(viewModel = viewModel) {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        SetUpNavigationGraph(viewModel)
                    }
                }
            }
        }
        stripeInitializer.initialize()
        handleDeepLink(intent?.data)
        startTokenMonitorService()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent.data)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val stripe = Stripe(this, PaymentConfiguration.getInstance(this).publishableKey)
        stripe.onPaymentResult(requestCode, data, object : ApiResultCallback<PaymentIntentResult> {
            override fun onSuccess(result: PaymentIntentResult) {
                val status = result.intent.status
                if (status == StripeIntent.Status.Succeeded) {
                    Log.d("Payment", "Payment succeeded")
                    paymentViewModel.upgradeUserToPremium()
                    Toast.makeText(this@MainActivity, "Payment successful", Toast.LENGTH_SHORT).show()
                } else {
                    val error = result.intent.lastPaymentError?.message ?: "Payment failed"
                    Log.e("Payment", "Payment failed: $error")
                    Toast.makeText(this@MainActivity, "Payment failed: $error", Toast.LENGTH_LONG).show()
                }
            }

            override fun onError(e: Exception) {
                Log.e("Payment", "Error: ${e.localizedMessage}")
                Toast.makeText(this@MainActivity, "Payment error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
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
}
