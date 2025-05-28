package com.ile.syrin_x.data.repository.musicsource

import android.util.Log
import com.ile.syrin_x.data.api.SpotifyAuthApi
import com.ile.syrin_x.data.database.SpotifyDao
import com.ile.syrin_x.data.model.entity.SpotifyUserToken
import com.ile.syrin_x.domain.repository.SpotifyAuthRepository
import com.ile.syrin_x.utils.EnvLoader
import com.ile.syrin_x.utils.GlobalContext
import com.ile.syrin_x.utils.createBae64CredentialsForAuthorizationFlow
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Named

class SpotifyAuthRepositoryImpl @Inject constructor(
    @Named("AccountApi") private val api: SpotifyAuthApi,
    private val dao: SpotifyDao
) : SpotifyAuthRepository {

    override suspend fun getToken(
        userUuid: String,
        authorization: String,
        code: String,
        redirectUri: String
    ): SpotifyUserToken? {
        return withContext(Dispatchers.IO) {
            try {
                if (getSpotifyToken(userUuid) == null) {
                    val response =
                        api.getToken(authorization, "authorization_code", code, redirectUri)
                    if (response.isSuccessful) {
                        response.body()?.let { token ->
                            val nowSec = System.currentTimeMillis() / 1000
                            val expiresAt = nowSec + token.expiresIn

                            val spotifyAuthToken = SpotifyUserToken(
                                userUuid,
                                token.accessToken,
                                token.refreshToken,
                                expiresAt
                            )
                            dao.insertToken(spotifyAuthToken)
                            Log.d("Spotify Login", "Spotify token saved.")
                            GlobalContext.Tokens.spotifyToken = token.accessToken
                            if (GlobalContext.loggedInMusicSources.find { x -> x == "Spotify" } == null) {
                                GlobalContext.loggedInMusicSources.add("Spotify")
                            }
                            return@withContext spotifyAuthToken
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d("Spotify Login Error", e.message.toString())
            }
            return@withContext null
        }
    }

    override suspend fun getSpotifyToken(userId: String): SpotifyUserToken? {
        return dao.getUserToken(userId)
    }

    override suspend fun deleteSpotifyToken(userId: String) {
        return withContext(Dispatchers.IO) {
            dao.deleteUser(userId)
            GlobalContext.loggedInMusicSources.remove("Spotify")
        }
    }

    override suspend fun refreshSpotifyAccessToken(userUuid: String, refreshToken: String?) {
        return withContext(Dispatchers.IO) {
            try {
                val credentials = createBae64CredentialsForAuthorizationFlow(
                    EnvLoader.spotifyClientId,
                    EnvLoader.spotifyClientSecret
                )
                val response = api.refreshToken(credentials, "refresh_token", refreshToken)
                if (response.isSuccessful) {
                    response.body()?.let { token ->
                        val nowSec = System.currentTimeMillis() / 1000
                        val expiresAt = nowSec + token.expiresIn

                        dao.updateToken(userUuid, token.accessToken, expiresAt)
                        Log.d("Spotify Token Refresh", "SoundCloud token was refreshed and saved.")
                        GlobalContext.Tokens.spotifyToken = token.accessToken
                    }
                }
            } catch (e: Exception) {
                Log.d("Spotify Login Error", e.message.toString())
            }
        }
    }
}