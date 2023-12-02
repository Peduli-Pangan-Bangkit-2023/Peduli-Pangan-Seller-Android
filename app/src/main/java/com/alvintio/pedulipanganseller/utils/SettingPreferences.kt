package com.alvintio.pedulipanganseller.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingPreferences(private val context: Context) {

    private val dataStore = context.dataStore

    suspend fun saveLoginStatus(isLoggedIn: Boolean) {
        val key = stringPreferencesKey("login_status")
        dataStore.edit { preferences ->
            preferences[key] = isLoggedIn.toString()
        }
    }

    fun getLoginStatus(): Flow<Boolean> {
        val key = stringPreferencesKey("login_status")
        return dataStore.data.map { preferences ->
            preferences[key]?.toBoolean() ?: false
        }
    }
}
