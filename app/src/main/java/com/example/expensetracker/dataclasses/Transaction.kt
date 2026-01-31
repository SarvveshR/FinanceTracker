package com.example.expensetracker.dataclasses

data class Transaction(
    val id: Int?=null,
    val type: Categories= Categories("",""),
    val date: String="",
    val time: String="",
    val description:String="",
    val amount: Int=0,
    val expense:Boolean=false,
    val income:Boolean=false
)