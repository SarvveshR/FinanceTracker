package com.example.expensetracker.auth

import android.util.Log
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.io.IOException

// Adds "Authorization: Bearer <access token>" to every request when someone is logged in
class AuthInterceptor(private val tokenStore: TokenStore) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { tokenStore.accessToken() }
            ?: return chain.proceed(chain.request())
        return chain.proceed(chain.request().withToken(token))
    }
}

// Called by OkHttp when a request comes back 401: the access token expired (every 15 min) or the session ended.
// Swaps the refresh token for a new pair and retries once. If the server says the session is over,
// it clears the saved tokens, which sends the app back to the login screen.
class TokenAuthenticator(
    private val tokenStore: TokenStore,
    private val authApi: AuthApi
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val failedToken = response.request.header("Authorization")?.removePrefix("Bearer ")
            ?: return null // wasn't a logged-in request
        if (response.priorResponse != null) return null // already retried once, give up

        // One refresh at a time; requests that failed together reuse the first one's result
        synchronized(this) {
            val currentToken = runBlocking { tokenStore.accessToken() } ?: return null
            if (currentToken != failedToken) {
                return response.request.withToken(currentToken)
            }

            val refreshToken = runBlocking { tokenStore.refreshToken() } ?: return null
            val refreshed = try {
                authApi.refresh(RefreshRequest(refreshToken)).execute()
            } catch (e: IOException) {
                Log.w(TAG, "Refresh failed, server unreachable", e)
                return null // keep the session; the call just fails like any network error
            }

            val body = refreshed.body()
            if (refreshed.isSuccessful && body != null) {
                runBlocking { tokenStore.save(body) }
                Log.d(TAG, "Access token refreshed")
                return response.request.withToken(body.accessToken)
            }

            if (refreshed.code() == 401) {
                Log.d(TAG, "Session ended on the server, logging out")
                runBlocking { tokenStore.clear() }
            }
            return null
        }
    }

    private companion object {
        const val TAG = "TokenAuthenticator"
    }
}

private fun Request.withToken(token: String): Request =
    newBuilder().header("Authorization", "Bearer $token").build()