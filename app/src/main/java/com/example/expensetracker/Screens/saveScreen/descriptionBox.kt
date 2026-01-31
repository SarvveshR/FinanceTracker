package com.example.expensetracker.Screens.saveScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CheckboxDefaults.colors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.expensetracker.ViewModel.ViewModel

@Composable
fun descriptionBox(viewModel: ViewModel, isDescBox:Boolean,isDescBoxafteruse:(Boolean)->Unit){
    var descBoxContentloc by remember{mutableStateOf<String>(viewModel.transaction.description)}
    var isDescBoxloc by remember{mutableStateOf<Boolean>(isDescBox)}


if(isDescBoxloc){
    Dialog(onDismissRequest = {isDescBoxloc=false},properties = DialogProperties(usePlatformDefaultWidth = false)){
        Card(   modifier = Modifier.fillMaxWidth(0.8f),
            colors = CardDefaults.cardColors(Color.White)
        ){
            Box(modifier = Modifier.fillMaxWidth().height(400.dp)){
                Text("Description",modifier=Modifier.align(Alignment.TopCenter).padding(10.dp), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = {isDescBoxafteruse(false)
                                    isDescBoxloc=false},modifier=Modifier.align(Alignment.TopEnd).size(50.dp).padding(5.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        modifier=Modifier.size(30.dp)
                    )

                }
                OutlinedTextField(modifier=Modifier.padding(10.dp).height(250.dp).align(Alignment.Center),
                    value = descBoxContentloc,
                    onValueChange ={descBoxContentloc=it} )


                IconButton(onClick = {viewModel.transaction=viewModel.transaction.copy(description = descBoxContentloc)
                    isDescBoxafteruse(false)
                    isDescBoxloc=false},modifier=Modifier.align(Alignment.BottomEnd).size(50.dp).padding(5
                    .dp)) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "Save",
                        modifier = Modifier.size(30.dp)
                    )

                }

            }


        }
    }

}





}