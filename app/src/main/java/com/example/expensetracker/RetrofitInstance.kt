package com.example.expensetracker

import android.content.Context
import com.example.expensetracker.auth.AuthApi
import com.example.expensetracker.auth.AuthInterceptor
import com.example.expensetracker.auth.TokenAuthenticator
import com.example.expensetracker.auth.TokenStore
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    // Set BASE_URL in local.properties (emulator: http://10.0.2.2:8081/, real phone: this computer's Wi-Fi IP)
    private const val BASE_URL = BuildConfig.BASE_URL

    private lateinit var tokenStore: TokenStore

    // Call once from MainActivity.onCreate, before any API call
    fun init(context: Context) {
        tokenStore = TokenStore(context)
    }

    // No token handling here: used only for refresh/logout
    val authApi: AuthApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }

    // Every request carries the access token; a 401 refreshes it and retries
    private val retrofit: Retrofit by lazy {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenStore))
            .authenticator(TokenAuthenticator(tokenStore, authApi))
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val service: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}