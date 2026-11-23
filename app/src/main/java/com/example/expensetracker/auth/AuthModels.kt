package com.example.expensetracker.auth

// Shapes agreed with the backend's /auth endpoints

data class GoogleAuthRequest(
    val idToken: String
)

// Body for /auth/refresh and /auth/logout
data class RefreshRequest(
    val refreshToken: String
)

data class AuthUser(
    val id: Long = 0,
    val email: String = "",
    val name: String = "",
    val pictureUrl: String? = null
)

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long, // seconds
    val user: AuthUser
)