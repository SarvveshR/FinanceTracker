package com.example.expensetracker.auth

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// Session endpoints. Runs on a plain client (no token, no auto-refresh), so a failing refresh can't loop.
interface AuthApi {

    // Blocking Call, because OkHttp's Authenticator runs synchronously
    @POST("/auth/refresh")
    fun refresh(@Body request: RefreshRequest): Call<AuthResponse>

    @POST("/auth/logout")
    suspend fun logout(@Body request: RefreshRequest): Response<Unit>
}