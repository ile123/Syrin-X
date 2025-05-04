package com.ile.syrin_x.data.model

data class CardDetails(
    val cardNumber: String,
    val expiryMonth: String,
    val expiryYear: String,
    val cvc: String,
    val postalCode: String
)