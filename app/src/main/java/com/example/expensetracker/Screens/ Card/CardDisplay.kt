package com.example.expensetracker.Screens.Card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.expensetracker.dataclasses.CardNetwork
import com.example.expensetracker.R
import com.example.expensetracker.ViewModel.ViewModel
import com.example.expensetracker.dataclasses.Card

@Composable
fun cardDisplay(viewModel: ViewModel, cardLst: List<Card>,goToCardsScreen: () -> Unit, goToAddCardsScreen: () -> Unit) {
    var cardLst = cardLst
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(10.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.padding(5.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {

                Text(
                    "Cards",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(10.dp)
                )



            }

            LazyColumn(modifier = Modifier
                .fillMaxSize()
                .weight(1f)) {
                items(cardLst.size) { index ->
                    Card(
                        colors = CardDefaults.cardColors(Color.Cyan),
                        modifier = Modifier
                            .height(220.dp)
                            .border(if(viewModel.selectedCardIndex==index){
                                BorderStroke(2.dp,Color.Green)
                            }
                            else{
                                BorderStroke(1.dp,Color.White)
                            })

                            .padding(10.dp).clickable(onClick={
                                viewModel.selectedCardIndex=index// assign index
                                viewModel.selectedCard=cardLst.get(viewModel.selectedCardIndex)// assign Card Object



                            }),


                    ) {

                        Box(modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp)
                          ) {
                            Column(modifier = Modifier.fillMaxSize()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(if(cardLst.get(index).debit){
                                        "₹" +" "+ "${cardLst.get(index).balance}"
                                    }
                                        else{
                                        "₹ "+ "${cardLst.get(index).creditLimit-cardLst.get(index).creditsUsed}"
                                        }, fontSize = 30.sp, color = Color.White)
                                    Image(
                                        painter = painterResource(
                                            if (cardLst.get(index).cardNetwork != CardNetwork.EmptyCard) {
                                                viewModel.newtworkhash.get(cardLst.get(index).cardNetwork)!!
                                            } else {
                                                R.drawable.visa
                                            }
                                        ),
                                        contentDescription = "${cardLst.get(index).cardNetwork}",
                                        modifier = Modifier.size(50.dp)
                                    )
                                }


                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.cardchip),

                                        contentDescription = "cardchip",
                                        modifier = Modifier.size(60.dp)
                                    )
                                    Image(
                                        painter = painterResource(R.drawable.contactless),

                                        contentDescription = "contactLess",
                                        modifier = Modifier.size(60.dp)
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    for (i in 1..4) {
                                        Text(
                                            cardLst.get(index).cardNo.substring(4 * i - 4, 4 * i),
                                            color = Color.White,
                                            fontSize = 25.sp
                                        )


                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(0.7f),
                                    horizontalArrangement = Arrangement.SpaceBetween,

                                ) {
                                    Column(modifier=Modifier.fillMaxHeight()) {
                                        Text("Card Holder", fontSize = 7.sp, color = Color.White)
                                        Text(cardLst.get(index).cardHolder,fontSize = 10.sp, color = Color.White)
                                    }
                                    Column(modifier=Modifier.fillMaxHeight()) {
                                        Text("Valid From", fontSize = 7.sp, color = Color.White)
                                        Text(cardLst.get(index).validFrom,fontSize =10.sp, color = Color.White)
                                    }
                                    Column(modifier=Modifier.fillMaxHeight()) {
                                        Text("Valid To", fontSize = 7.sp, color = Color.White)
                                        Text(cardLst.get(index).validTo,fontSize = 10.sp, color = Color.White)
                                    }

                                }


                            }

                        }


                    }
                }


            }

        }
        IconButton(onClick = (goToAddCardsScreen),modifier=Modifier.align(Alignment.BottomEnd).size(60.dp)) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Back",modifier = Modifier.size(50.dp))
        }


    }


}
