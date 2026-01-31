package com.example.expensetracker.Screens.Card

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
fun addCard(viewModel: ViewModel,goToCardsScreen:()->Unit) {
    var isDebit by remember { mutableStateOf(true) }
    var isCredit by remember { mutableStateOf(false) }
    var isKeyPadCardNo by remember { mutableStateOf(false) }
    var isKeyPadValidFrom by remember { mutableStateOf(false) }
    var isKeyPadValidThru by remember { mutableStateOf(false) }
    var isKeyPadBalance by remember { mutableStateOf(false) }
    var isKeyPadCreditLimit by remember { mutableStateOf(false) }
    var isKeyPadCreditUsed by remember { mutableStateOf(false) }


    var isDropDown by remember { mutableStateOf(false) }

    var cardNo by remember { mutableStateOf("XXXXXXXXXXXXXXXX") }
    var validFrom by remember { mutableStateOf("XX/XX") }
    var validThru by remember { mutableStateOf("XX/XX") }
    var balance by remember { mutableStateOf("") }
    var creditLimit by remember { mutableStateOf("") }
    var creditused by remember { mutableStateOf("") }
    var network : CardNetwork by remember { mutableStateOf(CardNetwork.EmptyCard) }
    var nametextField by remember { mutableStateOf("") }


    var iCN by remember { mutableStateOf(0) }
    var iVF by remember { mutableStateOf(0) }
    var iVT by remember { mutableStateOf(0) }
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
                    "ADD NEW CARD",
                    modifier = Modifier.padding(10.dp),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

            }

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(modifier = Modifier.wrapContentSize()) {
                    TextButton(onClick = {
                        isDebit = true
                        isCredit = false
                    }) {
                        Text(
                            "DEBIT CARD", fontSize = 20.sp, color = if (isDebit) {
                                DarkPurple
                            } else {
                                Color.Black
                            }, fontWeight = FontWeight.Medium
                        )
                    }
                    if (isDebit) {
                        Canvas(modifier = Modifier.width(130.dp)) {
                            val canvasWidth = size.width
                            val canvasHeight = size.height
                            drawLine(
                                start = Offset(x = 20f, y = 0f),
                                end = Offset(x = canvasWidth, y = 0f),
                                color = DarkPurple,
                                strokeWidth = 10f

                            )


                        }

                    }

                }
                Column() {
                    TextButton(onClick = {
                        isCredit = true
                        isDebit = false
                    }) {
                        Text(
                            "CREDIT CARD", fontSize = 20.sp, color = if (isCredit) {
                                DarkOrange
                            } else {
                                Color.Black
                            }, fontWeight = FontWeight.Medium
                        )
                    }
                    if (isCredit) {
                        Canvas(modifier = Modifier.width(150.dp)) {
                            val canvasWidth = size.width
                            val canvasHeight = size.height
                            drawLine(
                                start = Offset(x = 20f, y = 0f),
                                end = Offset(x = canvasWidth, y = 0f),
                                color = DarkOrange,
                                strokeWidth = 10f

                            )


                        }

                    }
                }


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


                        Box(
                            modifier = Modifier

                                .height(50.dp)

                                .fillMaxWidth()
                                .clickable(onClick = {
                                    isKeyPadValidFrom = false
                                    isKeyPadCardNo = false
                                    isKeyPadValidThru = false
                                    isKeyPadBalance = true
                                    isKeyPadCreditUsed = false
                                    isKeyPadCreditLimit = false
                                })
                                .border(width = 1.dp, color = Color.LightGray)
                        ) {
                            Text(
                                if (balance.isEmpty()) {
                                    "Enter Amount"
                                } else {
                                    "₹" + balance

                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.Center)
                            )


                        }


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
            if (isCredit) {
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
                                isKeyPadBalance = false
                                isKeyPadCreditUsed = false
                                isKeyPadCreditLimit = false


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
                            "Credit Limit",
                            fontSize = 10.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(10.dp)

                        )


                        Box(
                            modifier = Modifier

                                .height(50.dp)

                                .fillMaxWidth()
                                .clickable(onClick = {
                                    isKeyPadValidFrom = false
                                    isKeyPadCardNo = false
                                    isKeyPadValidThru = false
                                    isKeyPadBalance = false
                                    isKeyPadCreditUsed = false
                                    isKeyPadCreditLimit = true
                                })
                                .border(width = 1.dp, color = Color.LightGray)
                        ) {
                            Text(
                                if (creditLimit.isEmpty()) {
                                    "Enter Amount"
                                } else {
                                    "₹" + creditLimit

                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.Center)
                            )


                        }


                    }
                    Column(
                        modifier = Modifier
                            .padding(15.dp)
                            .wrapContentSize()
                    ) {
                        Text(
                            "Credits Used",
                            fontSize = 10.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(10.dp)

                        )


                        Box(
                            modifier = Modifier

                                .height(50.dp)

                                .fillMaxWidth()
                                .clickable(onClick = {
                                    isKeyPadValidFrom = false
                                    isKeyPadCardNo = false
                                    isKeyPadValidThru = false
                                    isKeyPadBalance = false
                                    isKeyPadCreditUsed = true
                                    isKeyPadCreditLimit = false
                                })
                                .border(width = 1.dp, color = Color.LightGray)
                        ) {
                            Text(
                                if (creditused.isEmpty()) {
                                    "Enter Amount"
                                } else {
                                    "₹" + creditused

                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.Center)
                            )


                        }


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
                                        isKeyPadCardNo = false
                                        isKeyPadValidThru = true
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
                        if (isDebit) {
                            300
                        } else {
                            190
                        }, viewModel, if (isDebit) {
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
                        if (isDebit) {
                            300
                        } else {
                            190
                        }, viewModel, if (isDebit) {
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
                        balance = balance.toLong(),
                        credit = false,
                        debit = true,
                        cardHolder = nametextField

                    )
                    viewModel.addCard(viewModel.card)

                    viewModel.card = Card()
                    goToCardsScreen()

                }, modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.BottomEnd)

            ) {
                Icon(
                    Icons.Default.Done, contentDescription = "Done", modifier = Modifier.size(40.dp)
                )

            }
        }
        else if (isCredit && !cardNo.isEmpty() && !validFrom.isEmpty() && !validThru.isEmpty() && !creditLimit.isEmpty() && !creditused.isEmpty() && !nametextField.isEmpty() && isKeyPadValidThru == false && isKeyPadCardNo == false && isKeyPadBalance == false && isKeyPadCreditUsed == false && isKeyPadCreditLimit == false) {
            IconButton(

                onClick = {
                    viewModel.card= viewModel.card.copy(
                        cardNetwork = network,
                        cardNo = cardNo,
                        validFrom = validFrom,
                        validTo = validThru,
                        credit = true,
                        debit = false,
                        creditLimit = creditLimit.toLong(),
                        creditsUsed = creditused.toLong(),
                        cardHolder = nametextField

                    )

                    viewModel.addCard(viewModel.card) // sending to backend
                    viewModel.card = Card()

                    goToCardsScreen()

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
