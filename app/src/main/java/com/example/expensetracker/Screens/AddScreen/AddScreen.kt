package com.example.expensetracker.Screens.AddScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.ViewModel.ViewModel
import com.example.expensetracker.ui.theme.DarkGreen
import com.example.expensetracker.ui.theme.DarkOrange
import com.example.expensetracker.ui.theme.DarkPurple

@Composable
fun addScreen( goToSaveScreen: (isIncome:Boolean,isExpense:Boolean) -> Unit,viewModel: ViewModel) {
    var list=viewModel.selectedCard.transaction?:emptyList()
    var isExpense by remember { mutableStateOf(false) }
    var isIncome by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize())

     {
        Column(
            modifier = Modifier
                .fillMaxSize()

        ) {
            Spacer(modifier = Modifier.padding(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {

                Text(
                    "Add",
                    modifier = Modifier.padding(10.dp),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )

            }
            Spacer(modifier = Modifier.padding(10.dp))
            if(viewModel.selectedCard.debit){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    Column(
                        modifier = Modifier
                            .size(height = 90.dp, width = 140.dp)
                            .clip(shape = RoundedCornerShape(10.dp))
                            .background(DarkPurple.copy(alpha = 0.2f))
                            .clickable(onClick = {
                                isIncome    = true
                                goToSaveScreen(isIncome, isExpense)
                            }),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Filled.AccountBalanceWallet, contentDescription = "Wallet", tint = DarkPurple,modifier=Modifier.size(35.dp))

                        Text("Add Income", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    }



                    Column(
                        modifier = Modifier
                            .size(height = 90.dp, width = 140.dp)
                            .clip(shape = RoundedCornerShape(10.dp))
                            .background(DarkOrange.copy(alpha = 0.4f))
                            .clickable(onClick = {
                                isExpense = true
                                goToSaveScreen(isIncome, isExpense)
                            }),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally)
                    {
                        Icon(imageVector = Icons.Filled.CreditCard,
                            contentDescription = "wallet",
                            tint=DarkOrange,
                            modifier=Modifier.size(35.dp)

                        )

                        Text("Add Expense", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    }
                }

            }
            else if(viewModel.selectedCard.credit){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {


                    Column(
                        modifier = Modifier
                            .size(height = 90.dp, width = 140.dp)
                            .clip(shape = RoundedCornerShape(10.dp))
                            .background(DarkOrange.copy(alpha = 0.4f))
                            .clickable(onClick = {
                                isExpense = true
                                goToSaveScreen(isIncome, isExpense)
                            }),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally)
                    {
                        Icon(imageVector = Icons.Filled.CreditCard,
                            contentDescription = "wallet",
                            tint=DarkOrange,
                            modifier=Modifier.size(35.dp)

                        )

                        Text("Add Expense", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    }
                }

            }

            Spacer(modifier = Modifier.padding(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text("Last Added", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            if(list.size>0){
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                ) {

                    items(count=list.size) { index ->

                        Spacer(modifier = Modifier.padding(5.dp))
                        Box(
                            modifier = Modifier
                                .height(70.dp)
                                .fillMaxWidth()


                        ) {

                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween

                            ) {
                                Row( modifier=Modifier.wrapContentSize()) {
                                    Spacer(modifier = Modifier.padding(5.dp))
                                    Image(
                                        painterResource(
                                            viewModel.categoryHash.get(list.get(index).type.id)!!

                                        ),
                                        contentDescription = "${list.get(index).type.name}",

                                        modifier=Modifier.size(50.dp) .clip(CircleShape)
                                    )

                                    Spacer(modifier = Modifier.padding(5.dp))

                                    Column(modifier= Modifier.fillMaxHeight().width(120.dp)) {
                                        Text(if(list.get(index).type.name.length<=17){
                                            list.get(index).type.name

                                        }
                                        else{
                                            list.get(index).type.name.substring(0,13)+"..."

                                        }


                                            , fontSize = 15.sp)
                                        Spacer(modifier = Modifier.padding(2.dp))
                                        Text(list.get(index).time, color = Color.Gray)

                                    }


                                }



                                Box(modifier=Modifier.fillMaxHeight()){
                                    if (list.get(index).income) {
                                        Text(
                                            "+" + " " + "₹${list.get(index).amount.toString()}",
                                            modifier = Modifier.padding(10.dp),
                                            fontSize = 15.sp,
                                            color = DarkGreen
                                        )

                                    } else if (list.get(index).expense) {
                                        Text(
                                            "-" + " " + "₹${list.get(index).amount.toString()}",
                                            modifier = Modifier.padding(10.dp),
                                            fontSize = 15.sp,
                                            color = Color.Red
                                        )


                                    }

                                }



                            }


                        }


                    }
                }

            }
            else{
                Box(modifier=Modifier.fillMaxSize()){
                    Text("No Recent Transaction ",modifier=Modifier.align(Alignment.Center))
                }


            }


        }


    }

}