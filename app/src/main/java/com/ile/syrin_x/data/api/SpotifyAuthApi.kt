package com.ile.syrin_x.data.api

import com.ile.syrin_x.data.model.AccessTokenResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface SpotifyAuthApi {
    @POST("api/token")
    suspend fun getToken(
        @Header("Authorization") authorization: String,
        @Field("grant_type") grantType: String,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String
    ): Response<AccessTokenResponse>
    /*@FormUrlEncoded
    @POST("api/token")
    suspend fun getAccessToken(
        @Field("grant_type") grantType: String,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("client_id") clientId: String,
        @Field("code_verifier") codeVerifier: String
    ): Response<AccessTokenResponse>

    @POST("api/token")
    @FormUrlEncoded
    fun refreshToken(
        @Field("grant_type") grantType: String = "refresh_token",
        @Field("refresh_token") refreshToken: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String
    ): Response<AccessTokenResponse>*/
}

