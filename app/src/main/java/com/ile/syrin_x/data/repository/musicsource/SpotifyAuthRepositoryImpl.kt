package com.ile.syrin_x.data.repository.musicsource

import android.util.Log
import com.ile.syrin_x.data.api.SpotifyAuthApi
import com.ile.syrin_x.data.database.SpotifyDao
import com.ile.syrin_x.data.model.SpotifyUserToken
import com.ile.syrin_x.domain.repository.SpotifyAuthRepository
import com.ile.syrin_x.utils.EnvLoader
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Named

class SpotifyAuthRepositoryImpl @Inject constructor(
    @Named("AccountApi") private val api: SpotifyAuthApi,
    private val dao: SpotifyDao
): SpotifyAuthRepository {

    override suspend fun getToken(userUuid: String, authorization: String, code: String, redirectUri: String): SpotifyUserToken? {
        return withContext(Dispatchers.IO) {
            try {
                if (getSpotifyToken(userUuid) == null) {
                    val response =
                        api.getToken(authorization, "authorization_code", code, redirectUri)
                    if (response.isSuccessful) {
                        response.body()?.let { token ->
                            val spotifyAuthToken = SpotifyUserToken(
                                userUuid,
                                token.accessToken,
                                token.refreshToken,
                                token.expiresIn
                            )
                            dao.insertToken(spotifyAuthToken)
                            Log.d("Spotify Login", "Spotify token saved.")
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

    override suspend fun refreshSpotifyAccessToken(userUuid: String, refreshToken: String?) {
        return withContext(Dispatchers.IO) {
            try {
                    val response = api.refreshToken("refresh_token", refreshToken, EnvLoader.spotifyClientId)
                    if (response.isSuccessful) {
                        response.body()?.let { token ->
                            dao.updateToken(userUuid, token.accessToken, token.expiresIn)
                            Log.d("Spotify Token Refresh", "SoundCloud token was refreshed and saved.")
                        }
                }
            } catch (e: Exception) {
                Log.d("Spotify Login Error", e.message.toString())
            }
        }
        }
    }