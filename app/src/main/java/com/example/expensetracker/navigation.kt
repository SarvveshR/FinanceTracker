package com.example.expensetracker

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.ui.anim.bounceClick
import com.example.expensetracker.ui.theme.IncomeColor
import com.example.expensetracker.ui.theme.ThemeMode
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.expensetracker.Screens.AddScreen.TransactionEditor
import com.example.expensetracker.Screens.Card.CardEditor
import com.example.expensetracker.Screens.Card.cardsScreen
import com.example.expensetracker.Screens.HomeScreen.homeScreen
import com.example.expensetracker.Screens.SettingScreen.Settings
import com.example.expensetracker.Screens.analytics.AnalyticsScreen
import com.example.expensetracker.ViewModel.ViewModel
import com.example.expensetracker.auth.AuthUser
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

// The four bottom-bar tabs. Adding a transaction is the + button on Home, not a tab.
private data class Tab(
    val route: Any,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

private val tabs = listOf(
    Tab(HomeScreen, "Home", Icons.Filled.Home, Icons.Outlined.Home),
    Tab(CardsScreen, "Cards", Icons.Filled.CreditCard, Icons.Outlined.CreditCard),
    Tab(Analytics, "Analytics", Icons.Filled.Analytics, Icons.Outlined.Analytics),
    Tab(Settings, "Settings", Icons.Filled.Settings, Icons.Outlined.Settings),
)

private fun NavDestination?.isOn(route: KClass<*>): Boolean =
    this?.hierarchy?.any { it.hasRoute(route) } == true

// Switches tabs without piling up screens: one copy of each tab, and back from any tab goes to Home
private fun NavHostController.openTab(route: Any) {
    navigate(route) {
        popUpTo(HomeScreen) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

private fun NavBackStackEntry.isTab(): Boolean = tabs.any { destination.isOn(it.route::class) }
private fun NavBackStackEntry.isEditor(): Boolean =
    destination.isOn(TransactionEditorScreen::class) || destination.isOn(CardEditorScreen::class)

private val TabEnter = fadeIn(tween(220, delayMillis = 60)) + scaleIn(tween(220, delayMillis = 60), initialScale = 0.97f)
private val TabExit = fadeOut(tween(100))

// Opaque backdrop for each destination, so two screens never show through each other mid-transition
@Composable
private fun ScreenSurface(content: @Composable () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) { content() }
}

// Pill-shaped bar floating above the bottom edge. The selected tab grows into a gradient pill
// showing its label; the others show just the icon.
@Composable
private fun FloatingNavBar(isSelected: (Tab) -> Boolean, onSelect: (Tab) -> Unit) {
    val shape = RoundedCornerShape(30.dp)
    Box(
        Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(18.dp, shape, ambientColor = IncomeColor.copy(alpha = 0.25f), spotColor = IncomeColor.copy(alpha = 0.35f))
                .clip(shape)
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { tab ->
                FloatingNavItem(tab, selected = isSelected(tab), onClick = { onSelect(tab) })
            }
        }
    }
}

@Composable
private fun FloatingNavItem(tab: Tab, selected: Boolean, onClick: () -> Unit) {
    val iconColor by animateColorAsState(
        if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "navIcon"
    )
    val pillAlpha by animateFloatAsState(if (selected) 1f else 0f, label = "navPill")

    Row(
        modifier = Modifier
            .height(52.dp)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF8B5CFF).copy(alpha = pillAlpha), IncomeColor.copy(alpha = pillAlpha))
                )
            )
            .bounceClick(pressedScale = 0.9f, onClick = onClick)
            // Width springs between icon-only and icon + label
            .animateContentSize(spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
            contentDescription = tab.title,
            tint = iconColor
        )
        if (selected) {
            Spacer(Modifier.width(8.dp))
            Text(
                tab.title,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
fun navigation(user: AuthUser, onLogout: () -> Unit, themeMode: ThemeMode, onThemeChange: (ThemeMode) -> Unit) {
    val navController = rememberNavController()
    val viewModel: ViewModel = viewModel()

    // Every success/error message from the ViewModel shows here, whichever screen is open
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(viewModel) {
        viewModel.messages.collect { message -> snackbarHostState.showSnackbar(message) }
    }

    // The highlighted tab comes from the screen actually showing, so it stays right after system back
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination
    val onTab = tabs.any { destination.isOn(it.route::class) }

    Scaffold(
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            AnimatedVisibility(
                visible = destination.isOn(HomeScreen::class),
                enter = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                FloatingActionButton(onClick = {
                    viewModel.startNewTransaction(isIncome = false, isExpense = true)
                    navController.navigate(TransactionEditorScreen(isEdit = false))
                }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add transaction")
                }
            }
        },
        bottomBar = {
            // Forms (Save, Edit, Add card...) get the whole screen
            AnimatedVisibility(
                visible = onTab,
                enter = slideInVertically(tween(250)) { it } + fadeIn(),
                exit = slideOutVertically(tween(200)) { it } + fadeOut()
            ) {
                FloatingNavBar(
                    isSelected = { destination.isOn(it.route::class) },
                    onSelect = { navController.openTab(it.route) }
                )
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
                // Tab ↔ tab: quick fade-through. Opening a form slides it in from the right; back slides it out.
                enterTransition = {
                    if (initialState.isTab() && targetState.isTab()) TabEnter
                    else slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(320)) + fadeIn(tween(320))
                },
                exitTransition = {
                    if (targetState.isEditor()) ExitTransition.KeepUntilTransitionsFinished
                    else if (initialState.isTab() && targetState.isTab()) TabExit
                    else slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(320)) { it / 4 } + fadeOut(tween(320))
                },
                popEnterTransition = {
                    if (initialState.isEditor()) EnterTransition.None
                    else if (initialState.isTab() && targetState.isTab()) TabEnter
                    else slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(320)) { it / 4 } + fadeIn(tween(320))
                },
                popExitTransition = {
                    if (initialState.isTab() && targetState.isTab()) TabExit
                    else slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(320)) + fadeOut(tween(320))
                },

                ) {
                composable<HomeScreen>() {
                    ScreenSurface {
                    homeScreen(
                        viewModel,
                        user = user,
                        goToCardScreen = { navController.openTab(CardsScreen) },
                        goToAddCardScreen = { navController.navigate(CardEditorScreen(isEdit = false)) },
                        goToEditTransaction = {
                            navController.navigate(TransactionEditorScreen(isEdit = true))
                        })
                    }
                }
                composable<CardsScreen> {
                    ScreenSurface {
                    cardsScreen(
                        viewModel,
                        onAddCard = { navController.navigate(CardEditorScreen(isEdit = false)) },
                        onEditCard = { navController.navigate(CardEditorScreen(isEdit = true)) }
                    )
                    }
                }
                // Add/edit transaction: slides up like a sheet, slides back down when closed
                composable<TransactionEditorScreen>(
                    enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tween(350)) },
                    popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tween(300)) }
                ) {
                    ScreenSurface {
                    val args = it.toRoute<TransactionEditorScreen>()
                    TransactionEditor(
                        viewModel = viewModel,
                        isEdit = args.isEdit,
                        onClose = { navController.popBackStack() },
                        goToAddCardScreen = { navController.navigate(CardEditorScreen(isEdit = false)) }
                    )
                    }
                }


                composable<Analytics> {
                    ScreenSurface {
                    AnalyticsScreen(vm=viewModel)
                    }
                }
                composable<Settings> {
                    ScreenSurface {
                    Settings(user = user, themeMode = themeMode, onThemeChange = onThemeChange, onLogout = onLogout)
                    }
                }

                // Add/edit card: same sheet-style slide as the transaction editor
                composable<CardEditorScreen>(
                    enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tween(350)) },
                    popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tween(300)) }
                ) {
                    ScreenSurface {
                    val args = it.toRoute<CardEditorScreen>()
                    CardEditor(viewModel, isEdit = args.isEdit, onClose = { navController.popBackStack() })
                    }
                }

            }
        }


    }

}


@Serializable
data object HomeScreen

@Serializable
data object CardsScreen

// One screen for adding (isEdit = false) and editing (isEdit = true) a transaction
@Serializable
data class TransactionEditorScreen(val isEdit: Boolean)

@Serializable
data object Analytics

@Serializable
data object Settings


// One screen for adding (isEdit = false) and editing (isEdit = true) a card
@Serializable
data class CardEditorScreen(val isEdit: Boolean)
