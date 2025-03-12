package com.ile.syrin_x.domain.repository

import com.ile.syrin_x.data.model.AccessTokenResponse
import com.ile.syrin_x.data.model.SpotifyUserToken

interface SpotifyAuthRepository {
    suspend fun getToken(userUuid: String, authorization: String, code: String, redirectUri: String): SpotifyUserToken
    suspend fun getSpotifyToken(userId: String): SpotifyUserToken
    /*fun getAuthUrl(): String
    suspend fun exchangeCodeForToken(code: String): AccessTokenResponse?
    //suspend fun refreshSpotifyToken(userId: String): SpotifyUserToken
    suspend fun saveSpotifyToken(userId: String, token: AccessTokenResponse)*/
}