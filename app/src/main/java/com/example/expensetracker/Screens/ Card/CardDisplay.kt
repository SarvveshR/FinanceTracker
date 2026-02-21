package com.example.expensetracker.Screens.Card

import android.graphics.Paint
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.dataclasses.CardNetwork
import com.example.expensetracker.R
import com.example.expensetracker.ViewModel.ViewModel
import com.example.expensetracker.dataclasses.Card
import com.example.expensetracker.dataclasses.DataErrorLoading

@Composable
fun cardDisplay(viewModel: ViewModel, dataErrorLoading: DataErrorLoading,goToCardsScreen: () -> Unit, goToAddCardsScreen: () -> Unit) {


   val gradient= Brush.sweepGradient(
       colors = listOf(
           Color(0xFF9090E0),
           Color(0xFFF08080),
           Color(0xFFB08EC0),
           Color(0xFFFF80C0),
           Color(0xFF9090E0)

       ),

   )




    if(dataErrorLoading.loading ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier=Modifier.align(Alignment.Center)){
                CircularProgressIndicator()
                Text("Loading")

            }


        }

    }
    else if(dataErrorLoading.error!=null){
        Box(modifier = Modifier.fillMaxSize()) {
                Text(dataErrorLoading.error,modifier= Modifier.align(Alignment.Center))
        }

    }
    else {
        val cardLst = dataErrorLoading.cards

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.padding(5.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {

                    Text(
                        "Cards",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(10.dp)
                    )


                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    items(cardLst.size) { index ->
                        Box(
                            modifier=Modifier.fillMaxWidth().height(220.dp).border(width = 2.dp, color = Color.Transparent, shape = RoundedCornerShape(5.dp)).background(
                                if(viewModel.selectedCardIndex==index){
                                    gradient
                                }
                                else{
                                    Brush.verticalGradient(colors=listOf(Color.Transparent,Color.Transparent))
                                },
                                shape = RoundedCornerShape(2.dp),
                            ))

                        {
                            Card(
                                colors = CardDefaults.cardColors(Color.Cyan),
                                modifier = Modifier
                                    .height(220.dp)

                                    .padding(10.dp).clickable(onClick = {
                                        viewModel.selectedCardIndex = index// assign index
                                        viewModel.selectedCard =
                                            cardLst.get(viewModel.selectedCardIndex)// assign Card Object


                                    }),
                                ) {

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(10.dp)
                                ) {
                                    Column(modifier = Modifier.fillMaxSize()) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(


                                                if (cardLst.get(index).debit) {
                                                    "₹" + " " + "${cardLst.get(index).balance}" + " "+"${cardLst.get(index).cardId}"
                                                } else {
                                                    "₹ " + "${
                                                        cardLst.get(index).creditLimit - cardLst.get(
                                                            index
                                                        ).creditsUsed
                                                    }" + " "+"${cardLst.get(index).cardId}"
                                                }, fontSize = 30.sp, color = Color.White
                                            )
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
                                                    cardLst.get(index).cardNo.substring(
                                                        4 * i - 4,
                                                        4 * i
                                                    ),
                                                    color = Color.White,
                                                    fontSize = 25.sp
                                                )


                                            }
                                        }
                                        Row(
                                            modifier = Modifier.fillMaxWidth(0.7f),
                                            horizontalArrangement = Arrangement.SpaceBetween,

                                            ) {
                                            Column(modifier = Modifier.fillMaxHeight()) {
                                                Text(
                                                    "Card Holder",
                                                    fontSize = 7.sp,
                                                    color = Color.White
                                                )
                                                Text(
                                                    cardLst.get(index).cardHolder,
                                                    fontSize = 10.sp,
                                                    color = Color.White
                                                )
                                            }
                                            Column(modifier = Modifier.fillMaxHeight()) {
                                                Text("Valid From", fontSize = 7.sp, color = Color.White)
                                                Text(
                                                    cardLst.get(index).validFrom,
                                                    fontSize = 10.sp,
                                                    color = Color.White
                                                )
                                            }
                                            Column(modifier = Modifier.fillMaxHeight()) {
                                                Text("Valid To", fontSize = 7.sp, color = Color.White)
                                                Text(
                                                    cardLst.get(index).validTo,
                                                    fontSize = 10.sp,
                                                    color = Color.White
                                                )
                                            }


                                        }


                                    }

                                }


                            }
                        }

                    }


                }

            }
            IconButton(
                onClick = (goToAddCardsScreen),
                modifier = Modifier.align(Alignment.BottomEnd).size(60.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Back",
                    modifier = Modifier.size(50.dp)
                )
            }


        }
    }
    }


