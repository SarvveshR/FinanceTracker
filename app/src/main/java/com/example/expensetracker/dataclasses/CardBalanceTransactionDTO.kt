package com.example.expensetracker.dataclasses

data class CardBalanceTransactionDTO(
    val balance: Long=0,
    val creditsUsed:Long=0,
    val responseTransactionDTO : Transaction
)
