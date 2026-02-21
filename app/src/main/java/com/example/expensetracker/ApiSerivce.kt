package com.example.expensetracker

import com.example.expensetracker.dataclasses.Card
import com.example.expensetracker.dataclasses.CardBalanceTransactionDTO
import com.example.expensetracker.dataclasses.Transaction
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @GET("/status")
   suspend fun getStatus(): String

   @POST("/card/add")
   suspend fun  addCard(@Body card:Card)

   @GET("/card/get")
   suspend fun getCards(): List<Card>


   @POST("/transaction/add/{cardId}")
   suspend fun addTransaction(@Path("cardId",) cardId:Int, @Body transaction: Transaction): CardBalanceTransactionDTO

   @PUT("/transaction/edit/{cardId}/{transactionId}")
   suspend fun editTransaction(@Body transaction: Transaction,@Path("cardId") cardId:Int, @Path("transactionId") transactionId:Int): Response<CardBalanceTransactionDTO>



}