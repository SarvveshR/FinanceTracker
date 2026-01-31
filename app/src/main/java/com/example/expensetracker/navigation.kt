package com.example.expensetracker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.expensetracker.Screens.Card.addCard
import com.example.expensetracker.Screens.Card.cardDisplay
import com.example.expensetracker.Screens.AddScreen.addScreen
import com.example.expensetracker.Screens.AnalyticsScreen.Analytics
import com.example.expensetracker.Screens.HomeScreen.homeScreen
import com.example.expensetracker.Screens.SettingScreen.Settings
import com.example.expensetracker.Screens.saveScreen.income_or_expense_Categories
import com.example.expensetracker.Screens.saveScreen.saveScreen
import com.example.expensetracker.ViewModel.ViewModel
import kotlinx.serialization.Serializable

@Composable
fun navigation() {
    val navController = rememberNavController()
    val viewModel: ViewModel = viewModel()

    Scaffold(    contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0),
        bottomBar = {
        NavigationBar() {
            viewModel.navBarItems.forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = viewModel.selectedIndex == index,
                    onClick = {
                        viewModel.selectedIndex = index
                        if (viewModel.selectedIndex == 0) {
                            navController.navigate(HomeScreen)
                        } else if (viewModel.selectedIndex == 1) {
                            navController.navigate(CardsScreen)
                        } else if (viewModel.selectedIndex == 2) {
                            navController.navigate(AddScreen)
                        } else if (viewModel.selectedIndex == 3) {
                            navController.navigate(Analytics)
                        }
                        else{
                            navController.navigate(Settings)
                        }

                    },
                    icon = {
                        Icon(
                            imageVector =
                                if (viewModel.selectedIndex == index) {
                                    item.selectedIcon
                                } else {
                                    item.unSelectedIcon
                                }, contentDescription = item.title

                        )

                    },
                    label = { Text(item.title) }


                )


            }


        }

    }) { innerpadding ->
        Box(
            modifier = Modifier
                .padding(innerpadding)
                .fillMaxSize()

        ) {
            NavHost(
                navController = navController,
                startDestination = HomeScreen,

                ) {
                composable<HomeScreen>() {
                    homeScreen(
                        viewModel,
                        goToAddScreen = { navController.navigate(AddScreen) },
                        goToCardScreen = {
                            navController.navigate(
                                CardsScreen
                            )
                        })

                }
                composable<CardsScreen> {
                    val cardLst by viewModel.cards.collectAsState()
                    cardDisplay(
                        viewModel,
                        cardLst=cardLst,
                        goToCardsScreen = { navController.navigate(AddCardsScreen) },
                        goToAddCardsScreen = {
                            navController.navigate(
                                AddCardsScreen
                            )
                        },)
                }
                composable<AddScreen>() {
                    addScreen(

                        goToSaveScreen = { income, expense ->

                            navController.navigate(
                                SaveScreen(
                                    income,
                                    expense
                                )
                            )
                        },
                        viewModel = viewModel
                    )

                }


                composable<Analytics>{
                    Analytics()
                }
                composable<Settings>{
                    Settings()
                }

                composable<SaveScreen>() {
                    val arguement = it.toRoute<SaveScreen>()
                    saveScreen(
                        viewModel,
                        arguement.isIncome,
                        arguement.isExpense,
                        goBackToAddScreen = {
                            navController.navigate(AddScreen)
                        },

                        goToCategoryScreen = { isIncome, isExpense ->
                            navController.navigate(
                                IncomeExpenseCategoryScreen(
                                    isIncome,
                                    isExpense,

                                    )
                            )
                        })

                }
                composable<IncomeExpenseCategoryScreen> {
                    val arguements = it.toRoute<IncomeExpenseCategoryScreen>()
                    income_or_expense_Categories(
                        viewModel,
                        arguements.isIncome,
                        arguements.isExpense,
                        goBackToSaveScreen = { isIncome, isExpense ->
                            navController.navigate(
                                SaveScreen(
                                    isIncome,
                                    isExpense,


                                    )
                            )
                        })


                }

                composable<AddCardsScreen> {
                    addCard(viewModel, goToCardsScreen = { navController.navigate(CardsScreen) })
                }




            }
        }


    }

}


@Serializable
data object HomeScreen
@Serializable
data object CardsScreen

@Serializable
data object AddScreen

@Serializable
data object Analytics

@Serializable
data object Settings






@Serializable
data class SaveScreen(
    var isIncome: Boolean,
    var isExpense: Boolean
)//var id :String, var name:String

@Serializable
data class IncomeExpenseCategoryScreen(var isIncome: Boolean, var isExpense: Boolean)

@Serializable
data object AddCardsScreen



