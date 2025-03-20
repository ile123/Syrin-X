package com.ile.syrin_x.domain.repository

import com.ile.syrin_x.data.model.AccessTokenResponse
import com.ile.syrin_x.data.model.SpotifyUserToken

interface SpotifyAuthRepository {
    suspend fun getToken(userUuid: String, authorization: String, code: String, redirectUri: String): SpotifyUserToken?
    suspend fun getSpotifyToken(userId: String): SpotifyUserToken?
    suspend fun refreshSpotifyAccessToken(userUuid: String, refreshToken: String?)
}