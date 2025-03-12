package com.ile.syrin_x.data.repository.musicsource

import com.ile.syrin_x.data.api.SpotifyAuthApi
import com.ile.syrin_x.data.database.SpotifyDao
import com.ile.syrin_x.data.model.SpotifyUserToken
import com.ile.syrin_x.domain.repository.SpotifyAuthRepository
import jakarta.inject.Inject
import javax.inject.Named

class SpotifyAuthRepositoryImpl @Inject constructor(
    @Named("AccountApi") private val api: SpotifyAuthApi,
    private val dao: SpotifyDao
): SpotifyAuthRepository {

    /*private val codeVerifier = generateCodeVerifier()
    private val codeChallenge = generateCodeChallenge(codeVerifier)

    override fun getAuthUrl(): String {
        return "https://accounts.spotify.com/authorize?" +
                "client_id=${EnvLoader.spotifyClientId}" +
                "&response_type=code" +
                "&redirect_uri=syrinx://app" +
                "&code_challenge_method=S256" +
                "&code_challenge=$codeChallenge"
    }

    override suspend fun exchangeCodeForToken(code: String): AccessTokenResponse? {
        return try {
            val response = api.getAccessToken(
                grantType = "authorization_code",
                code = code,
                redirectUri = "syrinx://app",
                clientId = EnvLoader.spotifyClientId,
                codeVerifier = codeVerifier
            )
            response.body()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun saveSpotifyToken(userId: String, token: AccessTokenResponse) {
        val newToken = SpotifyUserToken(userId, token.accessToken, token.refreshToken, token.expiresIn)
        dao.insertToken(newToken)
    }*/
    override suspend fun getToken(userUuid: String, authorization: String, code: String, redirectUri: String): SpotifyUserToken {
        val response = api.getToken(authorization, "authorization_code", code, redirectUri)
        if (response.isSuccessful) {
            response.body()?.let { token ->
                val spotifyAuthToken = SpotifyUserToken(userUuid, token.accessToken, token.refreshToken, token.expiresIn)
                dao.insertToken(spotifyAuthToken)
                return spotifyAuthToken
            }
        }
        throw Exception("Failed to get token")
    }

    override suspend fun getSpotifyToken(userId: String): SpotifyUserToken {
        return dao.getUserToken(userId) ?: throw Exception("No Spotify token found")
    }

}