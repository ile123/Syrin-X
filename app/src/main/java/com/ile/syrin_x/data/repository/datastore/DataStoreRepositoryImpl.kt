package com.ile.syrin_x.data.repository.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ile.syrin_x.domain.repository.DataStoreRepository
import kotlinx.coroutines.flow.first
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val PREFERENCES_NAME = "my_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_NAME)

class DataStoreRepositoryImpl @Inject constructor(
    private val context: Context
) : DataStoreRepository {

    override suspend fun putString(key: String, value: String) {
        val preferencesKey = stringPreferencesKey(key)
        context.dataStore.edit { prefs ->
            prefs[preferencesKey] = value
        }
    }

    override suspend fun putInt(key: String, value: Int) {
        val preferencesKey = intPreferencesKey(key)
        context.dataStore.edit { prefs ->
            prefs[preferencesKey] = value
        }
    }

    override suspend fun getString(key: String): String? {
        val preferencesKey = stringPreferencesKey(key)
        val prefs = context.dataStore.data.first()
        return prefs[preferencesKey]
    }

    override suspend fun getInt(key: String): Int? {
        val preferencesKey = intPreferencesKey(key)
        val prefs = context.dataStore.data.first()
        return prefs[preferencesKey]
    }

    override fun getStringFlow(key: String): Flow<String?> {
        val preferencesKey = stringPreferencesKey(key)
        return context.dataStore.data.map { prefs ->
            prefs[preferencesKey]
        }
    }

    override suspend fun putBoolean(key: String, value: Boolean) {
        val dataKey = booleanPreferencesKey(key)
        context.dataStore.edit { prefs ->
            prefs[dataKey] = value
        }
    }

    override suspend fun getBoolean(key: String): Boolean? {
        val dataKey = booleanPreferencesKey(key)
        val prefs = context.dataStore.data.first()
        return prefs[dataKey]
    }

    override fun getBooleanFlow(key: String): Flow<Boolean?> {
        val dataKey = booleanPreferencesKey(key)
        return context.dataStore.data.map { prefs ->
            prefs[dataKey]
        }
    }
}