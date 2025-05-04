package com.ile.syrin_x.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ile.syrin_x.data.api.StripeApi
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.domain.usecase.auth.GetUserUidUseCase
import com.ile.syrin_x.domain.usecase.user.UpgradeUserToPremiumUseCase
import com.ile.syrin_x.utils.EnvLoader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val stripeApi: StripeApi,
    private val upgradeUserToPremiumUseCase: UpgradeUserToPremiumUseCase,
    private val getUserUidUseCase: GetUserUidUseCase
) : ViewModel() {

    private val _paymentFlow = MutableSharedFlow<Response<String>>()
    val paymentFlow: SharedFlow<Response<String>> = _paymentFlow

    fun createPaymentIntent(amount: Int) {
        viewModelScope.launch {
            _paymentFlow.emit(Response.Loading)
            try {
                val params = mapOf(
                    "amount" to amount.toString(),
                    "currency" to "eur",
                    "automatic_payment_methods[enabled]" to "true"
                )
                val response = stripeApi.createPaymentIntent(
                    authorization = "Bearer ${EnvLoader.stripeSecretKey}",
                    fields = params
                )
                _paymentFlow.emit(Response.Success(response.clientSecret))
            } catch (e: Exception) {
                _paymentFlow.emit(Response.Error(e.localizedMessage ?: "PaymentIntent creation failed"))
            }
        }
    }

    fun upgradeUserToPremium() = viewModelScope.launch {
        getUserUidUseCase.invoke().collect { uid ->
            upgradeUserToPremiumUseCase(uid)
        }
    }
}
