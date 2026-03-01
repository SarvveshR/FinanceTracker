package com.example.expensetracker

import com.example.expensetracker.dataclasses.Card
import com.example.expensetracker.dataclasses.CardBalanceTransactionDTO
import com.example.expensetracker.dataclasses.Transaction
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @GET("/status")
   suspend fun getStatus(): String


   @POST("/card/add")
   suspend fun  addCard(@Body card:Card)
   // we get lsit of transaction so no need of making a seperate API
   @GET("/card/get")
   suspend fun getCards(): List<Card>


   //Transaction
   @POST("/transaction/add/{cardId}")
   suspend fun addTransaction(@Path("cardId",) cardId:Int, @Body transaction: Transaction): CardBalanceTransactionDTO

   @PUT("/transaction/edit/{cardId}/{transactionId}")
   suspend fun editTransaction(@Body transaction: Transaction,@Path("cardId") cardId:Int, @Path("transactionId") transactionId:Int): Response<CardBalanceTransactionDTO>

   @DELETE("/transaction/delete/{cardId}/{transactionId}")
   suspend fun deleteTransaction(@Path("cardId") cardId:Int, @Path("transactionId") transactionId:Int): Response<CardBalanceTransactionDTO>


   //card

   @PUT("/card/edit/{cardId}")
   suspend fun editCard(@Body card:Card,@Path("cardId") cardId:Int): Card
   @DELETE("/card/delete/{cardId}")
   suspend fun deleteCard(@Path("cardId") cardId:Int): Boolean


}