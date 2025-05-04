package com.ile.syrin_x.data.api

import com.ile.syrin_x.data.model.PaymentIntentResponse
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface StripeApi {
    @FormUrlEncoded
    @POST("payment_intents")
    suspend fun createPaymentIntent(
        @Header("Authorization") authorization: String,
        @FieldMap fields: Map<String, String>
    ): PaymentIntentResponse
}