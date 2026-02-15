package com.example.expensetracker.dataclasses

data class DataErrorLoading(
    val loading: Boolean = true,
    val error: String? = null,
    val cards: List<Card> =emptyList(),

)
