package com.alvintio.pedulipanganseller.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Const.preferenceName)

class SettingPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    private val token = stringPreferencesKey(Const.UserPreferences.UserToken.name)
    private val uid = stringPreferencesKey(Const.UserPreferences.UserUID.name)
    private val name = stringPreferencesKey(Const.UserPreferences.UserName.name)
    private val email = stringPreferencesKey(Const.UserPreferences.UserEmail.name)
    private val lastLogin = stringPreferencesKey(Const.UserPreferences.UserLastLogin.name)

    fun getUserToken(): Flow<String> = dataStore.data.map { it[token] ?: Const.preferenceDefaultValue }

    fun getUserUid(): Flow<String> = dataStore.data.map { it[uid] ?: Const.preferenceDefaultValue }

    fun getUserName(): Flow<String> = dataStore.data.map { it[name] ?: Const.preferenceDefaultValue }

    fun getUserEmail(): Flow<String> = dataStore.data.map { it[email] ?: Const.preferenceDefaultValue }

    fun getUserLastLogin(): Flow<String> = dataStore.data.map { it[lastLogin] ?: Const.preferenceDefaultDateValue }

    suspend fun saveLoginSession(userToken: String, userUid: String, userName:String, userEmail: String) {
        dataStore.edit { preferences ->
            preferences[token] = userToken
            preferences[uid] = userUid
            preferences[name] = userName
            preferences[email] = userEmail
        }
    }

    suspend fun clearLoginSession() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SettingPreferences? = null
        fun getInstance(dataStore: DataStore<Preferences>): SettingPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = SettingPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}