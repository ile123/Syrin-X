package com.ile.syrin_x.data.repository.musicsource

import com.ile.syrin_x.data.api.SoundCloudAuthApi
import com.ile.syrin_x.data.database.SoundCloudDao
import com.ile.syrin_x.data.model.SoundCloudUserToken
import com.ile.syrin_x.domain.repository.SoundCloudRepository
import javax.inject.Inject


class SoundCloudRepositoryImpl @Inject constructor(
    api: SoundCloudAuthApi,
    dao: SoundCloudDao
): SoundCloudRepository {
    override suspend fun getToken(
        userUuid: String,
        clientId: String,
        clientSecret: String,
        code: String,
        redirectUri: String
    ): SoundCloudUserToken {
        TODO("Not yet implemented")
    }

    override suspend fun getSoundCloudToken(userUuid: String): SoundCloudUserToken? {
        TODO("Not yet implemented")
    }

}

/*
class SoundCloudRepository @Inject constructor(
    private val apiService: SoundCloudApiService,
    private val tokenDao: SoundCloudTokenDao
) {
    suspend fun getToken(clientId: String, clientSecret: String, code: String, redirectUri: String): SoundCloudToken {
        val response = apiService.getToken(clientId, clientSecret, "authorization_code", redirectUri, code)
        if (response.isSuccessful) {
            response.body()?.let { token ->
                tokenDao.insertToken(token)
                return token
            }
        }
        throw Exception("Failed to get token")
    }

    suspend fun getStoredToken(): SoundCloudToken? {
        return tokenDao.getToken()
    }
    }*/
