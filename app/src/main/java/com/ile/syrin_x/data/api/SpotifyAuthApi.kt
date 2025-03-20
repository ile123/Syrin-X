package com.ile.syrin_x.data.api

import com.ile.syrin_x.data.model.AccessTokenResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface SpotifyAuthApi {
    @FormUrlEncoded
    @POST("api/token")
    suspend fun getToken(
        @Header("Authorization") authorization: String,
        @Field("grant_type") grantType: String,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String
    ): Response<AccessTokenResponse>

    @POST("api/token")
    @FormUrlEncoded
    suspend fun refreshToken(
        @Field("grant_type") grantType: String = "refresh_token",
        @Field("refresh_token") refreshToken: String?,
        @Field("client_id") clientId: String,
    ): Response<AccessTokenResponse>
}

