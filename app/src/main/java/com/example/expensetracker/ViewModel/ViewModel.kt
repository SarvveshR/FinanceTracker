package com.example.expensetracker.ViewModel
import android.util.Log
import com.example.expensetracker.dataclasses.CardNetwork

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.R
import com.example.expensetracker.RetrofitInstance
import com.example.expensetracker.dataclasses.Card
import com.example.expensetracker.dataclasses.Categories
import com.example.expensetracker.dataclasses.DataErrorLoading
import com.example.expensetracker.dataclasses.Transaction
import com.example.expensetracker.network.toUserMessage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ViewModel : ViewModel() {

    var expense by mutableStateOf("")

    //forCards
    var newtworkhash=hashMapOf<CardNetwork, Int >(CardNetwork.Visa to R.drawable.visa,CardNetwork.MasterCard to R.drawable.mastercard, CardNetwork.AmericanExpress to R.drawable.amex)

    // Success and error messages for the snackbar in navigation.kt. It lives above every screen,
    // so a message still shows after the screen that triggered it has navigated away.
    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 5)
    val messages: SharedFlow<String> = _messages



    //transaction adn //list of trans of a card

    var transaction by mutableStateOf<Transaction>(Transaction())
    var transactionIndex by mutableStateOf<Int>(0)



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
        "ec_fk" to R.drawable.family,
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

    fun showMessage(message: String) {
        _messages.tryEmit(message)
    }

    // Makes this card the one Home, Add and Save work with
    fun selectCard(index: Int) {
        val cardList = _cards.value.cards
        if (index in cardList.indices) {
            selectedCardIndex = index
            selectedCard = cardList[index]
        } else {
            selectedCardIndex = -1
            selectedCard = Card()
        }
    }

    // After the card list changes: keep the same card selected if it still exists, otherwise the first one
    private fun reselectCard() {
        val cardList = _cards.value.cards
        val previousId = selectedCard.cardId
        val index = cardList.indexOfFirst { it.cardId != null && it.cardId == previousId }
        selectCard(if (index >= 0) index else 0)
    }

    // Fresh draft for the Save screen: today's date, current time, keypad open
    fun startNewTransaction(isIncome: Boolean, isExpense: Boolean) {
        val now = LocalDateTime.now()
        transaction = Transaction(
            date = now.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
            time = now.format(DateTimeFormatter.ofPattern("hh:mm a")),
            income = isIncome,
            expense = isExpense
        )
        expense = ""
    }

    // Copies an existing transaction into the draft for the Edit screen
    fun startEditTransaction(index: Int) {
        transaction = selectedCard.transaction[index].copy()
        transactionIndex = index
        expense = transaction.amount.toString()
    }

    // Keypad input: the amount is kept in sync on every key press, so there's no separate "confirm" step
    fun appendAmountDigit(digit: Int) {
        if (expense.length >= MAX_AMOUNT_DIGITS) return
        if (expense.isEmpty() && digit == 0) return // no leading zeros
        expense += digit
        transaction = transaction.copy(amount = expense.toInt())
    }

    fun clearAmount() {
        expense = ""
        transaction = transaction.copy(amount = 0)
    }

    // Expense ↔ Income toggle. Categories differ per type, so switching clears the chosen one.
    fun setTransactionType(isIncome: Boolean) {
        if (transaction.income == isIncome && transaction.expense == !isIncome) return
        transaction = transaction.copy(
            income = isIncome,
            expense = !isIncome,
            type = Categories("", "")
        )
    }

    fun removeAmountDigit() {
        expense = expense.dropLast(1)
        transaction = transaction.copy(amount = expense.toIntOrNull() ?: 0)
    }

    // Checks the draft before it's sent. Returns an error message, or null if it's fine.
    private fun draftError(): String? {
        val t = transaction
        val card = selectedCard
        return when {
            card.cardId == null -> "Select a card first"
            t.amount <= 0 -> "Enter an amount"
            t.type.id.isBlank() -> "Choose a category"
            card.credit && t.expense && card.creditsUsed + t.amount > card.creditLimit -> "This would go over the card's credit limit"
            card.credit && t.income && t.amount > card.creditsUsed -> "Payment is more than the amount used on this card"
            card.debit && t.expense && t.amount > card.balance -> "Not enough balance on this card"
            else -> null
        }
    }

    // Save screen: validates, sends, and clears the draft. Returns false (and shows why) if it couldn't be saved.
    fun saveNewTransaction(): Boolean {
        draftError()?.let { showMessage(it); return false }
        addTransaction(transaction, selectedCard.cardId!!)
        transaction = Transaction()
        expense = ""
        return true
    }

    // Edit screen: same checks (balance rules are left to the server, since the old amount is already counted)
    fun saveEditedTransaction(): Boolean {
        val t = transaction
        when {
            t.amount <= 0 -> { showMessage("Enter an amount"); return false }
            t.type.id.isBlank() -> { showMessage("Choose a category"); return false }
            selectedCard.transaction.getOrNull(transactionIndex) == t -> { showMessage("Nothing changed"); return false }
        }
        editTransaction(t, selectedCard.cardId!!, t.id!!)
        transaction = Transaction()
        expense = ""
        return true
    }

    // Runs an API call. Any failure is logged and shown in the snackbar; the data on screen is left as it was.
    private fun launchApi(action: String, block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                block()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "$action failed", e)
                showMessage(e.toUserMessage())
            }
        }
    }

    // Puts an updated copy of the selected card back into the list
    private fun replaceSelectedCard(newCard: Card) {
        val newCardList = _cards.value.cards.toMutableList()
        newCardList[selectedCardIndex] = newCard
        _cards.value = _cards.value.copy(loading = false, error = null, cards = newCardList)
        selectedCard = newCard
    }

    // True while a card is being sent; the card form shows a spinner and ignores extra taps
    var isSavingCard by mutableStateOf(false)
        private set

    // Adds (editingId = null) or updates a card. Waits for the server, so if it's rejected the form stays open
    // with everything still filled in, and onSaved only runs on success.
    fun saveCard(card: Card, editingId: Int?, onSaved: () -> Unit) {
        if (isSavingCard) return
        isSavingCard = true
        viewModelScope.launch {
            try {
                if (editingId == null) {
                    RetrofitInstance.service.addCard(card)
                    showMessage("Card added")
                    getCards()
                } else {
                    val editedCard = RetrofitInstance.service.editCard(card, editingId)
                    replaceSelectedCard(editedCard.copy(transaction = selectedCard.transaction))
                    showMessage("Card updated")
                }
                onSaved()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Save card failed", e)
                showMessage(e.toUserMessage())
            } finally {
                isSavingCard = false
            }
        }
    }

    fun getCards(){
        viewModelScope.launch {
            // Full-screen spinner only on the first load; a refresh keeps the current cards visible
            if (_cards.value.cards.isEmpty()) {
                _cards.value = _cards.value.copy(loading = true, error = null)
            }
            try{
                val cardLst=RetrofitInstance.service.getCards()
                _cards.value = DataErrorLoading(loading = false, cards = cardLst)
                reselectCard()
                Log.d(TAG,"card list successfully fetched")
            }
            catch (e: CancellationException) {
                throw e
            }
            catch(e: Exception){
                Log.e(TAG,"Failed to fetch cards",e)
                if (_cards.value.cards.isEmpty()) {
                    // Nothing to show yet: the screens show this with a Retry button
                    _cards.value = _cards.value.copy(loading = false, error = e.toUserMessage())
                } else {
                    showMessage(e.toUserMessage())
                }
            }
        }
    }

    fun addTransaction( transaction:Transaction,cardId:Int) = launchApi("Add transaction") {
        val response = RetrofitInstance.service.addTransaction(cardId, transaction)
        val newTransaction = response.responseTransactionDTO
            ?: throw IllegalStateException("Backend returned no transaction")
        replaceSelectedCard(
            selectedCard.copy(
                balance = response.balance,
                creditsUsed = response.creditsUsed,
                transaction = selectedCard.transaction + newTransaction
            )
        )
        showMessage(if (newTransaction.income) "Income added" else "Expense added")
    }

    fun editTransaction(t: Transaction, cId:Int, tId:Int) = launchApi("Edit transaction") {
        val response = RetrofitInstance.service.editTransaction(t, cId, tId)
        val newTransaction = response.responseTransactionDTO
            ?: throw IllegalStateException("Backend returned no transaction")
        val newTransactionList = selectedCard.transaction.toMutableList()
        val index = newTransactionList.indexOfFirst { it.id == tId }
        if (index >= 0) newTransactionList[index] = newTransaction
        replaceSelectedCard(
            selectedCard.copy(
                balance = response.balance,
                creditsUsed = response.creditsUsed,
                transaction = newTransactionList
            )
        )
        showMessage("Transaction updated")
    }

    fun deleteTransaction( cId:Int, tId:Int) = launchApi("Delete transaction") {
        val response = RetrofitInstance.service.deleteTransaction(cId, tId)
        val newTransactionList = selectedCard.transaction.filter { it.id != tId }
        replaceSelectedCard(
            selectedCard.copy(
                balance = response.balance,
                creditsUsed = response.creditsUsed,
                transaction = newTransactionList
            )
        )
        showMessage("Transaction deleted")
    }


    fun deleteCard( cardId:Int) = launchApi("Delete card") {
        RetrofitInstance.service.deleteCard(cardId)
        val newCardList = _cards.value.cards.filter { it.cardId != cardId }
        _cards.value = _cards.value.copy(loading = false, error = null, cards = newCardList)
        selectCard(0) // first remaining card, or none
        showMessage("Card deleted")
    }


    private companion object {
        const val TAG = "ViewModel"
        const val MAX_AMOUNT_DIGITS = 9 // keeps the amount inside Int
    }
}
