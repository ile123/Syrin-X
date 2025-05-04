package com.ile.syrin_x.ui.screen

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.ile.syrin_x.R
import com.ile.syrin_x.data.model.CardDetails
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.ui.screen.common.MyAlertDialog
import com.ile.syrin_x.ui.screen.common.MyCircularProgress
import com.ile.syrin_x.viewModel.PaymentViewModel
import com.stripe.android.PaymentConfiguration
import com.stripe.android.Stripe
import com.stripe.android.model.Address
import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.model.PaymentMethod
import com.stripe.android.model.PaymentMethodCreateParams
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

@Composable
fun PaymentScreen(
    navController: NavController,
    viewModel: PaymentViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val stripe = remember {
        Stripe(context, PaymentConfiguration.getInstance(context).publishableKey)
    }
    val hostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val paymentFlowState = viewModel.paymentFlow

    var lastPaymentParams by remember { mutableStateOf<PaymentMethodCreateParams?>(null) }

    Scaffold(snackbarHost = { SnackbarHost(hostState = hostState) }) { paddingValues ->
        Content(
            paddingValues = paddingValues,
            paymentFlowState = paymentFlowState,
            onSubmit = { _, amount, params ->
                lastPaymentParams = params
                viewModel.createPaymentIntent(amount)
            },
            paymentSuccess = { clientSecret ->
                Log.d("PaymentScreen", "Client secret received: $clientSecret")
                lastPaymentParams?.let { paymentMethodParams ->
                    val confirmParams = ConfirmPaymentIntentParams.createWithPaymentMethodCreateParams(
                        paymentMethodParams,
                        clientSecret
                    )
                    (context as? ComponentActivity)?.let { activity ->
                        stripe.confirmPayment(activity, confirmParams)
                    } ?: run {
                        scope.launch {
                            hostState.showSnackbar("Error: Invalid context for Stripe")
                        }
                    }
                }
            },
            paymentError = { error ->
                scope.launch {
                    hostState.showSnackbar("Error: $error")
                }
            }
        )
    }
}


@Composable
fun Content(
    paddingValues: PaddingValues,
    paymentFlowState: SharedFlow<Response<String>>,
    onSubmit: (CardDetails, Int, PaymentMethodCreateParams) -> Unit,
    paymentSuccess: (String) -> Unit,
    paymentError: (String) -> Unit
) {
    var cardNumber by remember { mutableStateOf("") }
    var expiryMonth by remember { mutableStateOf("") }
    var expiryYear by remember { mutableStateOf("") }
    var cvc by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    fun submit() {
        val errors = mutableListOf<String>()

        if (cardNumber.isBlank()) errors.add("Card number is required.")
        if (expiryMonth.toIntOrNull() !in 1..12) errors.add("Invalid expiry month.")
        if (expiryYear.length != 2 || expiryYear.toIntOrNull() == null) errors.add("Invalid expiry year.")
        if (cvc.length !in 3..4) errors.add("CVC must be 3 or 4 digits.")
        if (postalCode.isBlank()) errors.add("Postal code is required.")
        if (amount.toIntOrNull() == null || amount.toInt() <= 0) errors.add("Amount must be a positive number.")

        if (errors.isNotEmpty()) {
            errorMessage = errors.joinToString("\n")
            showDialog = true
            return
        }

        val cardDetails = CardDetails(cardNumber, expiryMonth, expiryYear, cvc, postalCode)

        val cardParams = PaymentMethodCreateParams.Card.Builder()
            .setNumber(cardNumber)
            .setExpiryMonth(expiryMonth.toInt())
            .setExpiryYear("20$expiryYear".toInt())
            .setCvc(cvc)
            .build()

        val billingDetails = PaymentMethod.BillingDetails.Builder()
            .setAddress(
                Address.Builder()
                    .setPostalCode(postalCode)
                    .build()
            ).build()

        val paymentMethodParams = PaymentMethodCreateParams.create(cardParams, billingDetails)

        onSubmit(cardDetails, amount.toInt(), paymentMethodParams)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = cardNumber,
            onValueChange = { cardNumber = it },
            label = { Text("Card Number") },
            placeholder = { Text("4242 4242 4242 4242") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            OutlinedTextField(
                value = expiryMonth,
                onValueChange = { expiryMonth = it },
                label = { Text("MM") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = expiryYear,
                onValueChange = { expiryYear = it },
                label = { Text("YY") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = cvc,
            onValueChange = { cvc = it },
            label = { Text("CVC") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = postalCode,
            onValueChange = { postalCode = it },
            label = { Text("ZIP / Postal Code") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount (in cents)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = ::submit,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit Payment")
        }

        if (showDialog) {
            MyAlertDialog(
                onDismissRequest = { showDialog = false },
                onConfirmation = { showDialog = false },
                title = "Invalid Form",
                text = errorMessage,
                confirmButtonText = "OK",
                dismissButtonText = "Cancel",
                cancelable = true
            )
        }

        PaymentState(paymentFlowState, onSuccess = paymentSuccess, onError = paymentError)
    }
}

@Composable
fun PaymentState(
    paymentFlowState: SharedFlow<Response<String>>,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit
) {
    val isLoading = remember { mutableStateOf(false) }

    if (isLoading.value) {
        MyCircularProgress()
    }

    LaunchedEffect(Unit) {
        paymentFlowState.collect { result ->
            when (result) {
                is Response.Loading -> isLoading.value = true
                is Response.Error -> {
                    isLoading.value = false
                    onError(result.message)
                }
                is Response.Success -> {
                    isLoading.value = false
                    onSuccess(result.data)
                }
            }
        }
    }
}
