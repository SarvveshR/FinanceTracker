package com.example.expensetracker.Screens.saveScreen

import android.widget.Toast
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue

import androidx.compose.ui.graphics.Color

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.ViewModel.ViewModel
import com.example.expensetracker.dataclasses.Transaction
import com.vanpra.composematerialdialogs.rememberMaterialDialogState

@Composable
fun saveScreen(
    viewModel: ViewModel,
    isIncome: Boolean,
    isExpense: Boolean,

    goBackToAddScreen: () -> Unit,
    goToCategoryScreen: (Boolean, Boolean) -> Unit
) {
    var catname by remember { mutableStateOf("Choose Category") }
    var datePicker by remember { mutableStateOf(false) }
    var timepicker by remember { mutableStateOf(false) }
    var isDescBox by remember { mutableStateOf(false) }

    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()
    val toast = Toast.makeText(LocalContext.current, "Enter an amount", Toast.LENGTH_SHORT)








    Box(
        modifier = Modifier
            .fillMaxSize()

            .clickable(onClick = {

                viewModel.isKeyPad = false

            })
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(50.dp)
            ) {
                IconButton(onClick = { goBackToAddScreen() }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Back",
                        modifier = Modifier.size(35.dp)
                    )


                }

                Text(
                    "Add Amount",
                    modifier = Modifier.padding(10.dp),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.padding(5.dp))
            Row() {
                Spacer(modifier = Modifier.width(20.dp))
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                        .clickable(onClick = { viewModel.isKeyPad = true })
                        .height(100.dp)
                ) {
                    Text(
                        "Amount",
                        fontSize = 15.sp,
                        color = Color.Gray,
                        modifier = Modifier
                            .padding(5.dp)

                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()


                    ) {


                        Text(
                            "₹",
                            fontSize = 35.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Bottom)
                        )
                        Spacer(modifier = Modifier.padding(5.dp))

                        Text(
                            viewModel.expense,
                            fontSize = 45.sp,
                            fontWeight = FontWeight.Bold
                        )


                    }

                }


            }

            Canvas(modifier = Modifier.width(300.dp)) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                drawLine(
                    start = Offset(x = 50f, y = 0f),
                    end = Offset(x = canvasWidth, y = 0f),
                    color = Color.Black,
                    strokeWidth = 5f

                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            //cateopgry box
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .height(80.dp)
                    .border(width = 2.dp, shape = RoundedCornerShape(10.dp), color = Color.Gray)
                    .clickable(onClick = { goToCategoryScreen(isIncome, isExpense) })

            ) {

                Text(
                    if (isExpense) {
                        "Expenses made for"
                    } else {
                        "Income made from"

                    },
                    fontSize = 15.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(
                            5.dp
                        )
                )


                if (!viewModel.transaction.type.id.isEmpty()) {
                    catname = viewModel.transaction.type.name

                }
                Text(
                    "$catname",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(5.dp),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )


            }

            //date box
            Box(
                modifier = Modifier
                    .padding(10.dp)

                    .fillMaxWidth()
                    .height(80.dp)
                    .border(width = 2.dp, shape = RoundedCornerShape(10.dp), color = Color.Gray)
                    .clickable(onClick = {
                        datePicker = true
                        dateDialogState.show()
                    })

            ) {

                Text(
                    "Date ",
                    fontSize = 15.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(5.dp)
                )



                Text(
                    if (viewModel.transaction.date.equals("")) {
                        "Choose Date"
                    } else {
                        viewModel.transaction.date
                    },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(5.dp),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )


            }

            //time box
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .height(80.dp)
                    .border(width = 2.dp, shape = RoundedCornerShape(10.dp), color = Color.Gray)
                    .clickable(onClick = {
                        timepicker = true
                        timeDialogState.show()
                    })

            ) {

                Text(
                    "Time ",
                    fontSize = 15.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(5.dp)
                )


                Text(
                    if (viewModel.transaction.time == "") {
                        "Choose Time"
                    } else {
                        viewModel.transaction.time
                    },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(5.dp),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )


            }


            //Description  box
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .height(80.dp)
                    .border(width = 2.dp, shape = RoundedCornerShape(10.dp), color = Color.Gray)
                    .clickable(onClick = {
                        isDescBox = true

                    })

            ) {

                Text(
                    "Description ",
                    fontSize = 15.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(
                        4.dp
                    )


                )


                Text(
                    if (viewModel.transaction.description.equals("")) {
                        "Add Description"
                    } else {
                        if (viewModel.transaction.description.length < 20) {
                            viewModel.transaction.description
                        } else {
                            viewModel.transaction.description.substring(0, 20) + "..."

                        }
                    },
                    modifier = Modifier
                        .padding(4.dp),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )


            }
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = {
                        viewModel.transaction = viewModel.transaction.copy(
                            income = isIncome,
                            expense = isExpense
                        )//changing transaction id,isIncome and isExpense in object
                        viewModel.addTransaction(viewModel.transaction,viewModel.selectedCard.cardId!!)
                        viewModel.transaction= Transaction()
                        viewModel.expense=""
                        goBackToAddScreen()
                    }, modifier = Modifier
                        .align(Alignment.TopCenter)
                        .size(100.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Save",
                        modifier = Modifier.size(50.dp)
                    )

                }
                Text(
                    "Save",
                    modifier = Modifier.align(Alignment.BottomCenter),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }


        }


        //keypad
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            if (viewModel.isKeyPad) {
                keyPad(
                    300,
                    viewModel,
                    isIncome = isIncome,
                    isExpense = isExpense,

                    onChange = { value -> viewModel.expense += value },
                    backSpaceFunc = { viewModel.expense = viewModel.expense.dropLast(1) },
                    doneFunc = {
                        if (!viewModel.expense.isEmpty()) {
                            viewModel.transaction =
                                viewModel.transaction.copy(amount = viewModel.expense.toInt())
                            viewModel.isKeyPad = false
                        } else {
                            toast.show()

                        }
                    })


            }
        }
        // for date and time picker
        if (datePicker || timepicker) {
            timeAndDatePicker(viewModel, dateDialogState, timeDialogState, isDatePicker = { dp ->
                datePicker = dp
            }, isTimePicker = { tp ->
                timepicker = false

            })


        }



        if (isDescBox) {
            descriptionBox(viewModel, isDescBox, isDescBoxafteruse = {
                isDescBox = it

            })
        }


    }
}


//onClick = {

//   viewModel.transaction = viewModel.transaction.copy(
//       id = viewModel.id,
//        isIncome = isIncome,
//        isExpense = isExpense
//    )//changing transaction id,isIncome and isExpense in object
//   viewModel.list.add(viewModel.transaction)//adding transaction
//    viewModel.transaction =
//        Transaction()//changing viewModel transaction object to empty /default
//    viewModel.expense = ""// changing expense to default
//   goBackToAddScreen()



