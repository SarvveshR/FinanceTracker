package com.example.expensetracker.auth

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.BuildConfig
import com.example.expensetracker.RetrofitInstance
import com.example.expensetracker.network.apiError
import com.example.expensetracker.network.toUserMessage
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface AuthState {
    data object Loading : AuthState // reading saved tokens at startup
    data object LoggedOut : AuthState
    data class LoggedIn(val user: AuthUser) : AuthState
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenStore = TokenStore(application)

    // Holds the ViewModels of the logged-in screens (cards, transactions...). Replaced whenever the user
    // logs out or the session expires, so the next login starts empty instead of seeing the last user's data.
    var sessionViewModelStore by mutableStateOf(ViewModelStore())
        private set

    var isSigningIn by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Tells "user tapped Log out" apart from "the server ended the session" when the tokens disappear
    private var wasLoggedIn = false
    private var userLoggedOut = false

    val authState: StateFlow<AuthState> = tokenStore.user
        .map { user -> if (user == null) AuthState.LoggedOut else AuthState.LoggedIn(user) }
        .onEach { state ->
            if (state == AuthState.LoggedOut) {
                if (wasLoggedIn && !userLoggedOut) {
                    errorMessage = "Your session expired. Please sign in again."
                }
                userLoggedOut = false
                resetSessionViewModels()
            }
            wasLoggedIn = state is AuthState.LoggedIn
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, AuthState.Loading)

    private fun resetSessionViewModels() {
        sessionViewModelStore.clear()
        sessionViewModelStore = ViewModelStore()
    }

    override fun onCleared() {
        sessionViewModelStore.clear()
    }

    // 1. Google gives us an ID token  2. backend verifies it and returns its own tokens  3. we save them.
    // Saving flips authState to LoggedIn, which swaps the login screen for the app.
    fun signIn(activityContext: Context) {
        if (isSigningIn) return
        viewModelScope.launch {
            isSigningIn = true
            errorMessage = null
            try {
                val idToken = GoogleSignIn.getIdToken(activityContext)
                val response = RetrofitInstance.service.googleLogin(GoogleAuthRequest(idToken))
                tokenStore.save(response)
            } catch (e: GetCredentialCancellationException) {
                // user closed the account picker; not an error
            } catch (e: NoCredentialException) {
                errorMessage = "No Google account found on this device. Add one in Android Settings and try again."
            } catch (e: GetCredentialException) {
                Log.e(TAG, "Google sign-in failed", e)
                errorMessage = "Google sign-in failed: ${e.message}"
            } catch (e: HttpException) {
                Log.e(TAG, "Backend rejected login: ${e.apiError()}", e)
                errorMessage = if (e.code() == 401) {
                    "The server couldn't verify your Google sign-in. Try again."
                } else {
                    e.toUserMessage()
                }
            } catch (e: IOException) {
                Log.e(TAG, "Backend unreachable", e)
                errorMessage = "Can't reach the server at ${BuildConfig.BASE_URL}. Is the backend running?"
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected sign-in error", e)
                errorMessage = "Sign-in failed: ${e.message}"
            } finally {
                isSigningIn = false
            }
        }
    }

    // Leaves the app right away, then tells the backend to end the session so the tokens stop working there too
    fun logout(activityContext: Context) {
        viewModelScope.launch {
            val refreshToken = tokenStore.refreshToken()
            userLoggedOut = true
            tokenStore.clear()
            if (refreshToken != null) {
                try {
                    RetrofitInstance.authApi.logout(RefreshRequest(refreshToken))
                } catch (e: Exception) {
                    // Offline: the session stays valid on the server until it expires, but this phone no longer has it
                    Log.w(TAG, "Couldn't end the session on the server", e)
                }
            }
            try {
                GoogleSignIn.clearState(activityContext)
            } catch (e: Exception) {
                Log.w(TAG, "Couldn't clear Google credential state", e)
            }
        }
    }

    private companion object {
        const val TAG = "AuthViewModel"
    }
}