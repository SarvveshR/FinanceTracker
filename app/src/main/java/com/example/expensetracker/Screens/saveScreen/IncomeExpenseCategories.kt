package com.example.expensetracker.Screens.saveScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.ViewModel.ViewModel


@Composable
fun income_or_expense_Categories(
    viewModel: ViewModel,
    isIncome: Boolean,
    isExpense: Boolean,
    goBackToSaveScreen: (Boolean, Boolean) -> Unit,

) {


    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.padding(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(50.dp)
        ) {
            IconButton(onClick = { goBackToSaveScreen(isIncome, isExpense) }) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Back",
                    modifier = Modifier.size(35.dp)
                )


            }

            Text(
                if(isIncome){
                    "Income categories"
                }
                else{
                    "Expense categories"
                }
                ,
                modifier = Modifier.padding(10.dp),
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            )
        }



        Spacer(modifier = Modifier.padding(10.dp))
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(), columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(20.dp),
        ) {
            items(
                items = if (isIncome) {
                    viewModel.incomeList
                }  else  {
                    viewModel.expenseList
                }, key = { category -> category.id }) { category ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .aspectRatio(1f)
                        .background(Color.White)
                        .border(width = 2.dp, color = Color.LightGray)
                        .clickable(onClick = {
                            viewModel.transaction= viewModel.transaction.copy(type=viewModel.transaction.type.copy(category.id,category.name))

                            goBackToSaveScreen(
                                isIncome,
                                isExpense,
                            )
                        })
                ) {
                    Image(painterResource(
                        viewModel.categoryHash.get(category.id)!!
                    ),
                        contentDescription = "${category.name}",
                        modifier=Modifier.size(80.dp).align(Alignment.Center))

                    Text(
                        category.name,
                        fontSize = 10.sp,
                        modifier = Modifier.align(Alignment.BottomCenter),
                        fontWeight = FontWeight.Bold
                    )
                }

            }


        }

    }


}