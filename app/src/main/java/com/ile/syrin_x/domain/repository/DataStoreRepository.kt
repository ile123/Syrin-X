package com.ile.syrin_x.domain.repository

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun putString(key: String, value: String)
    suspend fun putInt(key: String, value: Int)
    suspend fun getString(key: String): String?
    suspend fun getInt(key: String): Int?
    fun getStringFlow(key: String): Flow<String?>

    suspend fun putBoolean(key: String, value: Boolean)
    suspend fun getBoolean(key: String): Boolean?
    fun   getBooleanFlow(key: String): Flow<Boolean?>
}