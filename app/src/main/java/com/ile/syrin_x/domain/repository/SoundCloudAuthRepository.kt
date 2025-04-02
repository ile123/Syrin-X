package com.ile.syrin_x.domain.repository

import com.ile.syrin_x.data.model.soundcloud.SoundCloudUserToken

interface SoundCloudAuthRepository {
    suspend fun getToken(userUuid: String, clientId: String, clientSecret: String, code: String, redirectUri: String): SoundCloudUserToken?
    suspend fun getSoundCloudToken(userUuid: String): SoundCloudUserToken?
    suspend fun refreshSoundCloudAccessToken(userUuid: String, refreshToken: String?)
}