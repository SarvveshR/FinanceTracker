package com.example.expensetracker.network

import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException

// The backend's error body: {"code":"INSUFFICIENT_BALANCE","message":"Not enough balance on this card","status":409,...}
data class ApiError(
    val code: String? = null,
    val message: String? = null,
    val status: Int = 0
)

private val gson = Gson()

// The backend's error for a failed call, or null if it wasn't an HTTP error with a readable body
fun Throwable.apiError(): ApiError? {
    if (this !is HttpException) return null
    val body = response()?.errorBody()?.string() ?: return null
    return runCatching { gson.fromJson(body, ApiError::class.java) }.getOrNull()
}

// One sentence to show the user for any failure: network down, server error, or a rule the backend enforced
fun Throwable.toUserMessage(): String = when (this) {
    is HttpException -> {
        val error = apiError()
        when {
            code() == 401 -> "Your session expired. Please sign in again."
            code() >= 500 && error?.code == "GOOGLE_UNAVAILABLE" -> error.message ?: "Couldn't reach Google. Try again."
            code() >= 500 -> "Something went wrong on the server. Try again."
            // 4xx messages from the backend are written for users ("Not enough balance on this card")
            !error?.message.isNullOrBlank() -> error!!.message!!
            else -> "Request failed (${code()})"
        }
    }
    is IOException -> "Can't reach the server. Check your connection and try again."
    else -> "Something went wrong. Try again."
}
