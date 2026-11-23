package com.example.expensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.Screens.LoginScreen.LoginScreen
import com.example.expensetracker.auth.AuthState
import com.example.expensetracker.auth.AuthViewModel
import com.example.expensetracker.settings.SettingsStore
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme
import com.example.expensetracker.ui.theme.ThemeMode
import com.example.expensetracker.ui.theme.isAppInDarkTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RetrofitInstance.init(applicationContext)
        enableEdgeToEdge()
        val settings = SettingsStore(applicationContext)
        setContent {
            val themeMode by settings.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
            val scope = rememberCoroutineScope()
            val darkTheme = isAppInDarkTheme(themeMode)

            ExpenseTrackerTheme(darkTheme = darkTheme) {
                val authViewModel: AuthViewModel = viewModel()
                val authState by authViewModel.authState.collectAsState()

                // Status/nav bar icons: dark on light screens, white on dark ones (login is always dark)
                val lightBars = !darkTheme && authState !is AuthState.LoggedOut
                SideEffect {
                    WindowCompat.getInsetsController(window, window.decorView).apply {
                        isAppearanceLightStatusBars = lightBars
                        isAppearanceLightNavigationBars = lightBars
                    }
                }

                // Saved tokens decide where the app opens: login screen or straight into the app
                when (val state = authState) {
                    AuthState.Loading -> Box(
                        Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    }
                    AuthState.LoggedOut -> LoginScreen(authViewModel)
                    is AuthState.LoggedIn -> {
                        // ViewModels created inside the app belong to this login and are thrown away on logout
                        val sessionStore = authViewModel.sessionViewModelStore
                        val sessionOwner = remember(sessionStore) {
                            object : ViewModelStoreOwner {
                                override val viewModelStore: ViewModelStore = sessionStore
                            }
                        }
                        CompositionLocalProvider(LocalViewModelStoreOwner provides sessionOwner) {
                            navigation(
                                user = state.user,
                                onLogout = { authViewModel.logout(this@MainActivity) },
                                themeMode = themeMode,
                                onThemeChange = { mode -> scope.launch { settings.setThemeMode(mode) } }
                            )
                        }
                    }
                }
            }
        }
    }
}
