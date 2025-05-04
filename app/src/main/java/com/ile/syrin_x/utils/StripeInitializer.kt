package com.ile.syrin_x.utils

import android.content.Context
import com.stripe.android.PaymentConfiguration
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StripeInitializer @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun initialize() {
        PaymentConfiguration.init(
            context,
            EnvLoader.stripePublishableKey
        )
    }
}