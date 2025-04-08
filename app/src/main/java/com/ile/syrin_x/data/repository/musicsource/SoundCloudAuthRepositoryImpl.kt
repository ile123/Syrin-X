package com.ile.syrin_x.data.repository.musicsource

import android.util.Log
import com.ile.syrin_x.data.api.SoundCloudAuthApi
import com.ile.syrin_x.data.database.SoundCloudDao
import com.ile.syrin_x.data.model.soundcloud.SoundCloudUserToken
import com.ile.syrin_x.domain.repository.SoundCloudAuthRepository
import com.ile.syrin_x.utils.EnvLoader
import com.ile.syrin_x.utils.GlobalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SoundCloudAuthRepositoryImpl @Inject constructor(
    private val api: SoundCloudAuthApi,
    private val dao: SoundCloudDao
): SoundCloudAuthRepository {
    override suspend fun getToken(
        userUuid: String,
        clientId: String,
        clientSecret: String,
        code: String,
        redirectUri: String
    ): SoundCloudUserToken? {
        return withContext(Dispatchers.IO) {
            try {
                if (getSoundCloudToken(userUuid) == null) {
                    val response = api.getToken(
                        clientId,
                        clientSecret,
                        "authorization_code",
                        redirectUri,
                        code
                    )
                    if (response.isSuccessful) {
                        response.body()?.let { result ->
                            val soundCloudUserToken = SoundCloudUserToken(
                                userUuid,
                                result.accessToken,
                                result.refreshToken,
                                result.expiresIn
                            )
                            dao.insertToken(soundCloudUserToken)
                            Log.d("SoundCloud Login", "SoundCloud token saved.")
                            GlobalContext.Tokens.soundCloudToken = result.accessToken
                            GlobalContext.loggedInMusicSources.add("SoundCloud")
                            return@withContext soundCloudUserToken
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d("SoundCloud Login Error", e.message.toString())
            }
            return@withContext null
        }
    }

    override suspend fun getSoundCloudToken(userUuid: String): SoundCloudUserToken? {
        return dao.getToken(userUuid)
    }

    override suspend fun refreshSoundCloudAccessToken(
        userUuid: String,
        refreshToken: String?
    ) {
        return withContext(Dispatchers.IO) {
            try {
                    val response = api.refreshToken("refresh_token", refreshToken, EnvLoader.soundCloudClientId, EnvLoader.soundCloudClientSecret)
                    if (response.isSuccessful) {
                        response.body()?.let { result ->
                            dao.updateToken(userUuid, result.accessToken, result.refreshToken, result.expiresIn)
                            Log.d("SoundCloud Token Refresh", "SoundCloud token was refreshed and saved.")
                            GlobalContext.Tokens.soundCloudToken = result.accessToken
                        }
                }
            } catch (e: Exception) {
                Log.d("SoundCloud Login Error", e.message.toString())
            }
        }
    }

}