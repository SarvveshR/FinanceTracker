package com.example.expensetracker.auth

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.authDataStore by preferencesDataStore(name = "auth")

// Keeps the backend's tokens on the phone so the user stays logged in across app restarts.
// The Google ID token is never stored: it's sent to the backend once and dropped.
class TokenStore(context: Context) {

    private val dataStore = context.applicationContext.authDataStore

    private object Keys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val ACCESS_EXPIRES_AT = longPreferencesKey("access_expires_at") // epoch millis
        val USER_ID = longPreferencesKey("user_id")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_PICTURE = stringPreferencesKey("user_picture")
    }

    // null when nobody is logged in
    val user: Flow<AuthUser?> = dataStore.data.map { prefs ->
        if (prefs[Keys.REFRESH_TOKEN] == null) {
            null
        } else {
            AuthUser(
                id = prefs[Keys.USER_ID] ?: 0,
                email = prefs[Keys.USER_EMAIL] ?: "",
                name = prefs[Keys.USER_NAME] ?: "",
                pictureUrl = prefs[Keys.USER_PICTURE]
            )
        }
    }

    suspend fun accessToken(): String? = dataStore.data.first()[Keys.ACCESS_TOKEN]

    suspend fun refreshToken(): String? = dataStore.data.first()[Keys.REFRESH_TOKEN]

    suspend fun save(response: AuthResponse) {
        dataStore.edit { prefs ->
            prefs[Keys.ACCESS_TOKEN] = response.accessToken
            prefs[Keys.REFRESH_TOKEN] = response.refreshToken
            prefs[Keys.ACCESS_EXPIRES_AT] = System.currentTimeMillis() + response.expiresIn * 1000
            prefs[Keys.USER_ID] = response.user.id
            prefs[Keys.USER_EMAIL] = response.user.email
            prefs[Keys.USER_NAME] = response.user.name
            response.user.pictureUrl?.let { prefs[Keys.USER_PICTURE] = it }
        }
    }

    suspend fun clear() {
        dataStore.edit { it.clear() }
    }
}