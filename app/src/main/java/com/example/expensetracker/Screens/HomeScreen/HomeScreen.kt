package com.example.expensetracker.Screens.HomeScreen

import androidx.compose.foundation.Image
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.expensetracker.dataclasses.CardNetwork

import com.example.expensetracker.R
import com.example.expensetracker.ViewModel.ViewModel
import com.example.expensetracker.dataclasses.Transaction
import com.example.expensetracker.ui.theme.DarkGreen

@Composable

fun homeScreen(viewModel: ViewModel, goToAddScreen: () -> Unit,goToCardScreen:()->Unit,goToEditSaveScreen:()->Unit) {
    val card = viewModel.selectedCard
    var isSeeAll by remember { mutableStateOf(false) }
    //for lazy column
    var list=viewModel.selectedCard.transaction?:emptyList()
    var isApi by remember{mutableStateOf(false)}





    Column(
        modifier = Modifier.fillMaxSize(),

        ) {

        Spacer(modifier = Modifier.padding(10.dp))
        Box(modifier = Modifier.fillMaxWidth()) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = {}) {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                }
                Text(
                    "Home",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(10.dp)
                )

                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notification"
                    )
                }
            }


        }
        Spacer(modifier = Modifier.padding(10.dp))
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if(card.cardNo.isEmpty()){
                    MaterialTheme.colorScheme.surfaceVariant
                }
                else{
                    Color.Cyan
                }
            ),
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth(0.9f)
                .align(Alignment.CenterHorizontally),

            ) {
            if(card.cardNo.isEmpty()){
                Box(modifier=Modifier.fillMaxSize()){
                    Text("No Cards Added Yet",modifier=Modifier.align(Alignment.Center))

                }
            }
            else{
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(if(card.debit){
                                "₹" +" "+ "${card.balance}"
                            }
                            else {
                                "₹ " + "${card.creditLimit - card.creditsUsed}"
                            }, fontSize = 30.sp, color = Color.White)
                            Image(
                                painter = painterResource(
                                    if (card.cardNetwork != CardNetwork.EmptyCard) {
                                        viewModel.newtworkhash.get(card.cardNetwork)!!
                                    } else {
                                        R.drawable.visa
                                    }
                                ),
                                contentDescription = "${card.cardNetwork}",
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
                                    card.cardNo.substring(4 * i - 4, 4 * i),
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
                                Text(card.cardHolder,fontSize = 10.sp, color = Color.White)
                            }
                            Column(modifier=Modifier.fillMaxHeight()) {
                                Text("Valid From", fontSize = 7.sp, color = Color.White)
                                Text(card.validFrom,fontSize =10.sp, color = Color.White)
                            }
                            Column(modifier=Modifier.fillMaxHeight()) {
                                Text("Valid To", fontSize = 7.sp, color = Color.White)
                                Text(card.validTo,fontSize = 10.sp, color = Color.White)
                            }

                        }


                    }

                }

            }



        }
        Spacer(modifier = Modifier.padding(5.dp))


        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
            ,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text("Transactions", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = {
                isSeeAll = true

            }) {
                Text("See All", color = Color.Gray)
            }
        }
        if(list.size>0){
            list=list.reversed()
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
            ) {

                items(count=list.size) { index ->
                    var isEditDeleteDropDown by remember{mutableStateOf(false)}

                    Spacer(modifier = Modifier.padding(5.dp))
                    Box(
                        modifier = Modifier
                            .height(70.dp)
                            .clickable(onClick={isEditDeleteDropDown=true})
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
                                    Column(){
                                        Text(list.get(index).date, color = Color.Gray)
                                    }
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


                            DropdownMenu(expanded=isEditDeleteDropDown, onDismissRequest = {isEditDeleteDropDown=false}, modifier = Modifier.align(
                                Alignment.Center)) {
                                IconButton(onClick={

                                    isEditDeleteDropDown=false
                                    viewModel.transaction=viewModel.selectedCard.transaction.get(list.size-1-index).copy()
                                    viewModel.transactionIndex=list.size-1-index
                                    viewModel.expense=viewModel.transaction.amount.toString()
                                    goToEditSaveScreen()


                                }){
                                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")

                                }
                                IconButton(onClick={
                                    isEditDeleteDropDown=false
                                    viewModel.transaction=viewModel.selectedCard.transaction.get(list.size-1-index).copy()
                                    viewModel.deleteTransaction(viewModel.selectedCard.cardId!!, viewModel.transaction.id!!)
                                    viewModel.transaction= Transaction()
                                    viewModel.transactionIndex=list.size-1-index


                                }){
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")

                                }



                        }

                    }


                }
            }

        }
        else {
            Box(modifier = Modifier.fillMaxSize()) {
                Text("No Recent Transaction ", modifier = Modifier.align(Alignment.Center))

            }
        }


        Button(onClick = {isApi=true}){
            Text("API")
        }
        if(isApi){
            viewModel.getStatus()
           Text(viewModel.status)
        }



    }




    }






