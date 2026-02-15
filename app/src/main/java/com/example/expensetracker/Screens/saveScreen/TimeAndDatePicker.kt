package com.example.expensetracker.Screens.saveScreen

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.expensetracker.ViewModel.ViewModel
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import okhttp3.internal.format
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@Composable
fun timeAndDatePicker(
    viewModel: ViewModel,
    dateDialogState: MaterialDialogState,
    timeDialogState: MaterialDialogState,
    isDatePicker: (Boolean) -> Unit,
    isTimePicker: (Boolean) -> Unit,
    localDate: String? ,
    localTime: String?
) {

    var pickedDate by remember {
        mutableStateOf(
            if (localDate == null) {
                LocalDate.now()
            } else {
                val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                val ld=LocalDate.parse(localDate,formatter)
                ld
            }
        )
    }
    var pickedTime by remember {
        mutableStateOf(
            if (localTime == null) {
                LocalTime.NOON
            } else {
                val formatter = DateTimeFormatter.ofPattern("hh:mm a")
                val lt= LocalTime.parse(localTime, formatter)
                lt
            }
        )
    }
    val okDateToast = Toast.makeText(LocalContext.current, "Date Picked", Toast.LENGTH_SHORT)
    val okTimeToast = Toast.makeText(LocalContext.current, "Time Picked", Toast.LENGTH_SHORT)
    val formatedDate by remember {
        derivedStateOf<String> {
            DateTimeFormatter.ofPattern("dd-MM-yyyy").format(pickedDate)
        }
    }
    val formatedTime by remember {
        derivedStateOf<String> {
            DateTimeFormatter.ofPattern("hh:mm a").format(pickedTime)
        }
    }

    MaterialDialog(
        dialogState = dateDialogState,
        buttons = {
            positiveButton("Ok") {
                okDateToast.show()
                viewModel.transaction = viewModel.transaction.copy(date = formatedDate)
                isDatePicker(false)
            }
            negativeButton("Cancel")

        }
    ) {
        datepicker(
            initialDate = pickedDate,
            title = "Pick a date",
        ) {
            pickedDate = it
        }

    }
    MaterialDialog(
        dialogState = timeDialogState,
        buttons = {
            positiveButton("Ok") {
                okTimeToast.show()

                viewModel.transaction = viewModel.transaction.copy(time = formatedTime)
                isTimePicker(false)


            }
            negativeButton("Cancel")

        }
    ) {
        timepicker(
            initialTime = pickedTime,
            title = "Pick time",
            timeRange = LocalTime.MIN..LocalTime.MAX
        ) {
            pickedTime = it


        }

    }


}
