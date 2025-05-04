package com.ile.syrin_x.data.model

import com.google.gson.annotations.SerializedName

data class PaymentIntentResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("object")
    val objectType: String,

    @SerializedName("amount")
    val amount: Int,

    @SerializedName("client_secret")
    val clientSecret: String,

    @SerializedName("currency")
    val currency: String,

    @SerializedName("status")
    val status: String
)
