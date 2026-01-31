package com.example.expensetracker.Screens.saveScreen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.DoneOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.ViewModel.ViewModel
import com.example.expensetracker.ui.theme.DarkOrange
import com.example.expensetracker.ui.theme.DarkPurple

@Composable
fun keyPad( height:Int ,viewModel: ViewModel,isIncome:Boolean,isExpense:Boolean,onChange:(String)->Unit, backSpaceFunc:()->Unit,doneFunc:()->Unit){
    val toast= Toast.makeText(LocalContext.current,"Enter an amount", Toast.LENGTH_SHORT)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(height.dp)
        , colors = CardDefaults.cardColors(Color.White)

    ) {
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Fixed(3)
        ) {
            items(viewModel.keyPadLst) { item ->
                Box(
                    modifier = Modifier

                        .size(height = (height/4).dp, width = 130.dp)
                        .clickable(onClick = {
                            if (item is Int) {

                                onChange(item.toString())
                            } else if (item == Icons.Default.Backspace) {
                                backSpaceFunc()                                 }
                            else {
                                doneFunc()

                            }
                        })
                ) {
                    if (item is Int) {
                        Text("$item", fontSize = (height/7).sp, fontWeight = FontWeight.Bold,modifier=Modifier.align(
                            Alignment.Center))
                    } else if (item == Icons.Default.Backspace) {
                        Icon(
                            imageVector = Icons.Default.Backspace,
                            contentDescription = "Backspace",
                            modifier=Modifier.align (Alignment.Center).size((height/7).dp),

                            )
                    } else {
                        if(isIncome){
                            Icon(
                                imageVector = Icons.Filled.DoneOutline,
                                contentDescription = "Done",
                                tint = DarkPurple,
                                modifier=Modifier.align (Alignment.Center).size((height/7).dp),
                            )
                        }
                        else if(isExpense){
                            Icon(
                                imageVector = Icons.Filled.DoneOutline,
                                contentDescription = "Done",
                                tint = DarkOrange,
                                modifier=Modifier.align (Alignment.Center).size((height/7).dp),
                            )

                        }
                    }

                }

            }
        }

    }
}