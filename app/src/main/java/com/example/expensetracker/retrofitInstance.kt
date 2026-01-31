package com.example.expensetracker

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitInstance {
    private var retrofit =
        Retrofit.Builder().baseUrl("http://192.168.29.217:8081").addConverterFactory(
            ScalarsConverterFactory.create()).addConverterFactory(
            GsonConverterFactory.create()
        ).build()

        val service= retrofit.create(ApiService::class.java)
}