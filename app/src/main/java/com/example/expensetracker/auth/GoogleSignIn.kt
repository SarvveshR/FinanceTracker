package com.example.expensetracker.auth

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.example.expensetracker.BuildConfig
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

// Talks to Google through Android's Credential Manager.
// `context` must be an Activity: Google shows its account picker on top of it.
object GoogleSignIn {

    // Shows the "Sign in with Google" sheet and returns the Google ID token for our backend.
    // The token's audience is the *Web* client ID, because the backend is the one that verifies it.
    suspend fun getIdToken(context: Context): String {
        val option = GetSignInWithGoogleOption.Builder(BuildConfig.GOOGLE_WEB_CLIENT_ID).build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(option)
            .build()

        val credential = CredentialManager.create(context).getCredential(context, request).credential

        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            return GoogleIdTokenCredential.createFrom(credential.data).idToken
        }
        throw IllegalStateException("Unexpected credential type: ${credential.type}")
    }

    // Makes the next sign-in show the account picker again instead of reusing the last account
    suspend fun clearState(context: Context) {
        CredentialManager.create(context).clearCredentialState(ClearCredentialStateRequest())
    }
}