package com.example.expensetracker.Screens.Card


import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.dataclasses.CardNetwork
import com.example.expensetracker.R
import com.example.expensetracker.Screens.saveScreen.keyPad
import com.example.expensetracker.ViewModel.ViewModel
import com.example.expensetracker.dataclasses.Card
import com.example.expensetracker.ui.theme.DarkOrange
import com.example.expensetracker.ui.theme.DarkPurple

@Composable
fun EditDebitCard(viewModel: ViewModel,goToCardsScreen:()->Unit) {
    var isDebit by remember { mutableStateOf(true) }
    var isCredit by remember { mutableStateOf(false) }
    var isKeyPadCardNo by remember { mutableStateOf(false) }
    var isKeyPadValidFrom by remember { mutableStateOf(false) }
    var isKeyPadValidThru by remember { mutableStateOf(false) }
    var isKeyPadBalance by remember { mutableStateOf(false) }
    var isKeyPadCreditLimit by remember { mutableStateOf(false) }
    var isKeyPadCreditUsed by remember { mutableStateOf(false) }
    val context= LocalContext.current

    var isDropDown by remember { mutableStateOf(false) }

    var cardNo by remember { mutableStateOf(viewModel.card.cardNo) }
    var validFrom by remember { mutableStateOf(viewModel.card.validFrom) }
    var validThru by remember { mutableStateOf(viewModel.card.validTo) }
    var balance by remember { mutableStateOf(viewModel.card.balance.toString()) }
    var creditLimit by remember { mutableStateOf(viewModel.card.creditLimit.toString())}
    var creditused by remember { mutableStateOf("") }
    var network : CardNetwork by remember { mutableStateOf(viewModel.card.cardNetwork) }
    var nametextField by remember { mutableStateOf(viewModel.card.cardHolder) }


    var iCN by remember { mutableStateOf(15) }
    var iVF by remember { mutableStateOf(5) }
    var iVT by remember { mutableStateOf(5) }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.wrapContentSize()
        ) {
            Spacer(modifier = Modifier.padding(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(60.dp)
            ) {
                IconButton(onClick = {goToCardsScreen() }) {
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowLeft,
                        contentDescription = "Back",
                        modifier = Modifier.size(35.dp)
                    )
                }

                Text(
                    "EDIT DEBIT CARD",
                    modifier = Modifier.padding(10.dp),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

            }




            Spacer(modifier = Modifier.padding(10.dp))
            if (isDebit) {
                Column(
                    modifier = Modifier
                        .padding(15.dp)
                        .fillMaxWidth()
                ) {

                    Text(
                        "Card Number",
                        fontSize = 10.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(10.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = {
                                isKeyPadValidFrom = false
                                isKeyPadValidThru = false
                                isKeyPadCardNo = true


                            }), horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (i in 1..4) {

                            Box(
                                modifier = Modifier
                                    .width(70.dp)
                                    .height(50.dp)
                                    .border(width = 1.dp, color = Color.LightGray)

                            ) {

                                Text(
                                    cardNo.substring(4 * i - 4, 4 * i),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.Center)
                                )

                            }
                        }


                    }


                    Column(
                        modifier = Modifier
                            .padding(15.dp)
                            .wrapContentSize()
                    ) {
                        Text(
                            "Balance",
                            fontSize = 10.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(10.dp)

                        )





                    }
                    Row(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Column(modifier = Modifier.wrapContentSize()) {
                            Text(
                                "Valid From",
                                fontSize = 10.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(10.dp)
                            )


                            Box(
                                modifier = Modifier
                                    .height(50.dp)
                                    .width(100.dp)
                                    .clickable(onClick = {
                                        isKeyPadValidFrom = true
                                        isKeyPadValidThru = false
                                        isKeyPadCardNo = false
                                        isKeyPadBalance = false
                                        isKeyPadCreditUsed = false
                                        isKeyPadCreditLimit = false
                                    })
                                    .border(width = 1.dp, color = Color.LightGray)
                            ) {
                                Text(
                                    validFrom,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.Center)
                                )


                            }


                        }
                        Column(modifier = Modifier.wrapContentSize()) {
                            Text(
                                "Network",
                                fontSize = 10.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(10.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .height(50.dp)
                                    .width(100.dp)
                                    .clickable(onClick = {
                                        isDropDown = true
                                    })
                                    .border(width = 1.dp, color = Color.LightGray)
                            ) {
                                if (network == CardNetwork.EmptyCard) {
                                    Text(

                                        "Choose",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.align(Alignment.Center)
                                    )

                                } else {
                                    Image(
                                        painter = painterResource(
                                            viewModel.newtworkhash.get(network)!!
                                        ),
                                        contentDescription = "$network",
                                        modifier = Modifier
                                            .size(
                                                if (network.equals(CardNetwork.AmericanExpress)) {
                                                    80.dp
                                                } else {
                                                    40.dp
                                                }
                                            )
                                            .align(Alignment.Center)

                                    )
                                }


                            }

                            DropdownMenu(
                                expanded = isDropDown, onDismissRequest = { isDropDown = false }) {
                                DropdownMenuItem(onClick = {
                                    isDropDown = false
                                    network = CardNetwork.Visa
                                }, text = {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.visa),
                                            contentDescription = "visa",
                                            modifier = Modifier.size(40.dp)
                                        )
                                    }


                                })
                                DropdownMenuItem(onClick = {
                                    isDropDown = false
                                    network = CardNetwork.MasterCard
                                }, text = {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.mastercard),
                                            contentDescription = "MasterCard",
                                            modifier = Modifier.size(40.dp)
                                        )
                                    }


                                }


                                )
                                DropdownMenuItem(onClick = {
                                    isDropDown = false
                                    network = CardNetwork.AmericanExpress
                                }, text = {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.amex),
                                            contentDescription = "Amex",
                                            modifier = Modifier.size(80.dp)
                                        )

                                    }


                                }

                                )


                            }


                        }




                        Column(modifier = Modifier.wrapContentSize()) {
                            Text(
                                "Valid Thru",
                                fontSize = 10.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(10.dp)

                            )


                            Box(
                                modifier = Modifier
                                    .height(50.dp)
                                    .width(100.dp)
                                    .clickable(onClick = {
                                        isKeyPadValidFrom = false
                                        isKeyPadValidThru = true
                                        isKeyPadCardNo = false
                                        isKeyPadBalance = false
                                        isKeyPadCreditUsed = false
                                        isKeyPadCreditLimit = false

                                    })
                                    .border(width = 1.dp, color = Color.LightGray)
                            ) {
                                Text(
                                    validThru,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.Center)
                                )


                            }


                        }


                    }
                    Column(
                        modifier = Modifier
                            .padding(15.dp)
                            .wrapContentSize()
                    ) {
                        Text(
                            "Card Holder",
                            fontSize = 10.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(10.dp)

                        )

                        Box(
                            modifier = Modifier
                                .height(50.dp)
                                .fillMaxWidth()
                                .border(width = 1.dp, color = Color.LightGray)
                        ) {
                            TextField(
                                value = nametextField,
                                onValueChange = { nametextField = it },
                                modifier = Modifier.align(Alignment.Center),

                                textStyle = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                ),
                                placeholder = {
                                    Text(
                                        "Enter Name", fontSize = 12.sp, fontWeight = FontWeight.Bold
                                    )
                                }


                            )

                        }


                    }


                }

            }



        }
        if (isDebit || isCredit) {
            if (isKeyPadCardNo) {
                Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                    keyPad(
                        300, viewModel, if (isDebit) {
                            true
                        } else {
                            false
                        }, if (isCredit) {
                            true
                        } else {
                            false
                        }, onChange = {
                            if (iCN < 16) {
                                cardNo = cardNo.replaceRange(iCN, iCN + 1, it)
                                iCN++
                            }

                        }, backSpaceFunc = {
                            if (iCN >= 1) {
                                cardNo = cardNo.replaceRange(iCN - 1, iCN, "X")
                                iCN--
                            }
                        }, doneFunc = { isKeyPadCardNo = false })
                }
            } else if (isKeyPadValidFrom) {
                Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                    keyPad(
                        190, viewModel, if (isDebit) {
                            true
                        } else {
                            false
                        }, if (isCredit) {
                            true
                        } else {
                            false
                        }, onChange = {
                            if (iVF < 5 && iVF != 2) {
                                validFrom = validFrom.replaceRange(iVF, iVF + 1, it)
                                iVF++
                            } else if (iVF == 2) {
                                iVF++
                                validFrom = validFrom.replaceRange(iVF, iVF + 1, it)
                                iVF++

                            }

                        }, backSpaceFunc = {
                            if (iVF >= 1 && iVF != 3) {
                                validFrom = validFrom.replaceRange(iVF - 1, iVF, "X")
                                iVF--
                            } else if (iVF == 3) {
                                iVF--
                                validFrom = validFrom.replaceRange(iVF - 1, iVF, "X")
                                iVF--
                            }
                        }, doneFunc = { isKeyPadValidFrom = false })
                }
            } else if (isKeyPadValidThru) {
                Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                    keyPad(
                      190,
                        viewModel, if (isDebit) {
                            true
                        } else {
                            false
                        }, if (isCredit) {
                            true
                        } else {
                            false
                        }, onChange = {
                            if (iVT < 5 && iVT != 2) {
                                validThru = validThru.replaceRange(iVT, iVT + 1, it)
                                iVT++
                            } else if (iVT == 2) {
                                iVT++
                                validThru = validThru.replaceRange(iVT, iVT + 1, it)
                                iVT++

                            }

                        }, backSpaceFunc = {
                            if (iVT >= 1 && iVT != 3) {
                                validThru = validThru.replaceRange(iVT - 1, iVT, "X")
                                iVT--
                            } else if (iVT == 3) {
                                iVT--
                                validThru = validThru.replaceRange(iVT - 1, iVT, "X")
                                iVT--
                            }
                        }, doneFunc = { isKeyPadValidThru = false })
                }
            } else if (isKeyPadBalance) {
                Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                    keyPad(
                        300, viewModel, if (isDebit) {
                            true
                        } else {
                            false
                        }, if (isCredit) {
                            true
                        } else {
                            false
                        }, onChange = {
                            balance += it


                        }, backSpaceFunc = {
                            balance = balance.dropLast(1)
                        }, doneFunc = { isKeyPadBalance = false })
                }
            } else if (isKeyPadCreditLimit) {
                Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                    keyPad(
                        300, viewModel, if (isDebit) {
                            true
                        } else {
                            false
                        }, if (isCredit) {
                            true
                        } else {
                            false
                        }, onChange = {
                            creditLimit += it


                        }, backSpaceFunc = {
                            creditLimit = creditLimit.dropLast(1)
                        }, doneFunc = { isKeyPadCreditLimit = false })
                }
            } else if (isKeyPadCreditUsed) {
                Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                    keyPad(
                        300, viewModel, if (isDebit) {
                            true
                        } else {
                            false
                        }, if (isCredit) {
                            true
                        } else {
                            false
                        }, onChange = {
                            creditused += it


                        }, backSpaceFunc = {
                            creditused = creditused.dropLast(1)
                        }, doneFunc = { isKeyPadCreditUsed = false })
                }
            }


        }
        if (isDebit && !cardNo.isEmpty() && !validFrom.isEmpty() && !validThru.isEmpty() && !balance.isEmpty() && !nametextField.isEmpty() && isKeyPadValidThru == false && isKeyPadCardNo == false && isKeyPadBalance == false && isKeyPadCreditUsed == false && isKeyPadCreditLimit == false) {

            IconButton(

                onClick = {
                    viewModel.card=viewModel.card.copy(
                        cardNetwork =network ,
                        cardNo = cardNo,
                        validFrom = validFrom,
                        validTo = validThru,
                        cardHolder = nametextField

                    )
                    if(viewModel.card==viewModel.selectedCard){
                        Toast.makeText(context,"Change something",Toast.LENGTH_SHORT).show()
                    }
                    else{

                        viewModel.editCard(viewModel.card, viewModel.selectedCard.cardId!!)

                        viewModel.card = Card()
                        goToCardsScreen()

                    }

                }, modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.BottomEnd)

            ) {
                Icon(
                    Icons.Default.Done, contentDescription = "Done", modifier = Modifier.size(40.dp)
                )

            }
        }



    }


}
