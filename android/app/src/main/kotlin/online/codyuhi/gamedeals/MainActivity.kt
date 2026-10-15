package online.codyuhi.gamedeals

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import online.codyuhi.gamedeals.ui.GameDealsApp
import online.codyuhi.gamedeals.ui.MainViewModel
import online.codyuhi.gamedeals.ui.theme.ThemeMode

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        val container = (application as GameDealsApplication).container

        setContent {
            val vm: MainViewModel = viewModel(
                factory = MainViewModel.factory(
                    settingsRepository = container.settingsRepository,
                    repository = container.gameDealsRepository,
                ),
            )

            val state by vm.uiState.collectAsStateWithLifecycle()
            val systemDark = isSystemInDarkTheme()
            val dark = when (state.themeMode) {
                ThemeMode.System -> systemDark
                ThemeMode.Light -> false
                ThemeMode.Dark -> true
            }

            LaunchedEffect(dark) {
                val style = if (dark) {
                    SystemBarStyle.dark(Color.TRANSPARENT)
                } else {
                    SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                }
                enableEdgeToEdge(statusBarStyle = style, navigationBarStyle = style)
            }

            GameDealsApp(viewModel = vm)
        }
    }
}
