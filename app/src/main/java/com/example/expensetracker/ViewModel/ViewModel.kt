package com.example.expensetracker.ViewModel
import android.util.Log
import com.example.expensetracker.dataclasses.CardNetwork

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.R
import com.example.expensetracker.RetrofitInstance
import com.example.expensetracker.dataclasses.BottomNavigationBar
import com.example.expensetracker.dataclasses.Card
import com.example.expensetracker.dataclasses.Categories
import com.example.expensetracker.dataclasses.DataErrorLoading
import com.example.expensetracker.dataclasses.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ViewModel : ViewModel() {

    //Bottom navigation Bar
    var status  by mutableStateOf("")
    val navBarItems= listOf<BottomNavigationBar>(
        BottomNavigationBar(title = "Home", selectedIcon = Icons.Filled.Home, unSelectedIcon = Icons.Outlined.Home),
        BottomNavigationBar(title = "Cards", selectedIcon = Icons.Filled.CreditCard, unSelectedIcon = Icons.Outlined.CreditCard),
        BottomNavigationBar(title = "Add ", selectedIcon = Icons.Filled.AddCircle, unSelectedIcon = Icons.Outlined.AddCircle),
        BottomNavigationBar(title = "Analytics", selectedIcon = Icons.Filled.Analytics, unSelectedIcon = Icons.Outlined.Analytics),
        BottomNavigationBar(title = "Settings", selectedIcon = Icons.Filled.Settings, unSelectedIcon = Icons.Outlined.Settings)

    )
    var selectedIndex by mutableStateOf(0)
    //keypad in SaveScreen
    val keyPadLst =
        listOf<Any>(1, 2, 3, 4, 5, 6, 7, 8, 9, Icons.Default.Backspace, 0, Icons.Filled.Done)
    var isKeyPad by mutableStateOf(false)
    var expense by mutableStateOf("")


    //forCards
    var newtworkhash=hashMapOf<CardNetwork, Int >(CardNetwork.Visa to R.drawable.visa,CardNetwork.MasterCard to R.drawable.mastercard, CardNetwork.AmericanExpress to R.drawable.amex)





    //transaction adn //list of trans of a card

    var transaction by mutableStateOf<Transaction>(Transaction())


    private val _cards = mutableStateOf<DataErrorLoading>(DataErrorLoading())// read and write
    val cards: State<DataErrorLoading> = _cards// only read
    init{
        getCards()
    }











    //while making card
    var card by mutableStateOf<Card>(Card())
    //slected card and index
    var  selectedCard by mutableStateOf(Card())

    var selectedCardIndex by mutableStateOf(-1)

    //Card(cardNetwork = CardNetwork.Visa, cardNo = "1234567890123456",)
    val categoryHash = hashMapOf<String, Int>(
        "ic_sw" to R.drawable.sales_wages,
        "ic_bi" to R.drawable.bussinessincome,
        "ic_in" to R.drawable.investments,
        "ic_ri" to R.drawable.rentalincome,
        "ic_rr" to R.drawable.refundandreimbursements,
        "ic_oi" to R.drawable.passive,
        "ec_ho" to R.drawable.housing,
        "ec_tr" to R.drawable.transportation,
        "ec_fg" to R.drawable.food,
        "ec_bu" to R.drawable.bills,
        "ec_hm" to R.drawable.medical,
        "ec_lp" to R.drawable.personal,
        "ec_fi" to R.drawable.insurance,
        "ec_ed" to R.drawable.education,
        "ec_en" to R.drawable.entertainment,
        "ec_tl" to R.drawable.travel,
        "ec_fk" to R.drawable. family,
        "ec_mi" to R.drawable.miscellaneous

    )


    val expenseList =
        mutableStateListOf<Categories>(
            Categories("ec_ho", "Housing"),
            Categories("ec_tr", "Transportation"),
            Categories("ec_fg", "Food & Groceries"),
            Categories("ec_bu", "Bills & Utilities"),
            Categories("ec_hm", "Health & Medical"),
            Categories("ec_lp", "Lifestyle & Personal"),
            Categories("ec_fi", "Finance & Insurance"),
            Categories("ec_ed", "Education"),
            Categories("ec_en", "Entertainment"),
            Categories("ec_tl", "Travel"),
            Categories("ec_fk", "Family"),
            Categories("ec_mi", "Miscellaneous"),

            )

    val incomeList = mutableStateListOf<Categories>(
        Categories("ic_sw", "Salary / Wages"),
        Categories("ic_bi", "Business Income"),
        Categories("ic_in", "Investments"),
        Categories("ic_ri", "Rental Income"),
        Categories("ic_rr", "Refunds & Reimbursements"),
        Categories("ic_oi", "Other Income"),
    )

    fun getStatus(){
        viewModelScope.launch{
           status=RetrofitInstance.service.getStatus()

        }
    }

    fun addCard(card: Card){
        viewModelScope.launch {
            RetrofitInstance.service.addCard(card)
            getCards()
        }
    }

    fun getCards(){
        viewModelScope.launch {
            var dataErrorLoading= DataErrorLoading()

            try{
                val cardLst=RetrofitInstance.service.getCards()
                dataErrorLoading=dataErrorLoading.copy(loading = false, cards = cardLst)
                _cards.value=dataErrorLoading
                Log.d("Successful","card list successfully fetched")

            }
            catch(e: Exception){
                dataErrorLoading=dataErrorLoading.copy(error="Error Fetching Cards ${e.message}")
                _cards.value=dataErrorLoading

                Log.e("unsuccessful","Failed to fetch",e)



            }
        }
    }

    fun addTransaction( transaction:Transaction,cardId:Int){
        viewModelScope.launch {
            var dataErrorLoading= DataErrorLoading()

            try{
                val newCardBalanceTransactionDTO= RetrofitInstance.service.addTransaction(cardId,transaction)
                val newTransaction= newCardBalanceTransactionDTO.responseTransactionDTO
                if (newTransaction == null) {
                    Log.e("AddTransaction", "Backend returned null transaction")

                }
                var transactionList:List<Transaction> =selectedCard.transaction
                transactionList= transactionList+newTransaction


                val newCard:Card=selectedCard.copy( balance = newCardBalanceTransactionDTO.balance, creditsUsed = newCardBalanceTransactionDTO.creditsUsed, transaction=transactionList)
                val newCardList= cards.value.cards.toMutableList()
                newCardList.set(selectedCardIndex,newCard)
                _cards.value=dataErrorLoading.copy(loading=false,cards=newCardList.toList())
                selectedCard=newCardList.get(selectedCardIndex)
                Log.d("Successful","transaction successfully fetched")

            }
            catch(e: Exception){
                _cards.value= dataErrorLoading.copy(error=" Error to Fetch ${e.message}")
                Log.e("unsuccessful","Failed to fetch",e)

            }
        }

    }






}