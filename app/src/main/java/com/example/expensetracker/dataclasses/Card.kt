package com.example.expensetracker.dataclasses

data class Card(
    val cardId:Int?=null,
    val cardNetwork: CardNetwork = CardNetwork.EmptyCard,
    val cardHolder:String="",
    val cardNo: String="",
    val validFrom:String="",
    val validTo:String="",
    val debit: Boolean=false,
    val credit:Boolean=false,
    val balance:Long=0,
    val creditLimit:Long=0,
    val creditsUsed:Long=0,
    val transaction:List<Transaction> =emptyList(),


    )
