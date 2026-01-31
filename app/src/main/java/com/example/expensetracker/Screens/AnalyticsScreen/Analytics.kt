package com.example.expensetracker.Screens.AnalyticsScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Analytics(){
    Box(modifier= Modifier.fillMaxSize().padding(15.dp)){
    Row(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter), horizontalArrangement = Arrangement.Center) {

        Text(
            "Analytics",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(10.dp)
        )



    }

        Text("UNDER CONSTRUCTION", modifier = Modifier.align(Alignment.Center))
    }
}