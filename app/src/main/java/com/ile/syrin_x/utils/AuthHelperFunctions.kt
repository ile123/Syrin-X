package com.ile.syrin_x.utils

import android.net.Uri
import android.util.Base64

fun getSpotifyAuthorizationUrl(clientId: String, redirectUri: String, scopes: List<String>): String {
    val scopeString = scopes.joinToString(" ")
    return "https://accounts.spotify.com/authorize?client_id=$clientId&response_type=code&redirect_uri=$redirectUri&scope=$scopeString"
}

fun getSoundCloudAuthorizationUrl(clientId: String, redirectUri: String, scopes: List<String>): String {
    val scopeString = scopes.joinToString(" ")
    return "https://soundcloud.com/connect?client_id=$clientId&response_type=code&redirect_uri=$redirectUri&scope=$scopeString"
}

fun extractAuthorizationCode(uri: Uri): String? {
    return uri.getQueryParameter("code")
}

fun createBae64CredentialsForAuthorizationFlow(clientId: String, clientSecret: String): String {
    val credentials = "$clientId:$clientSecret"
    return "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
}