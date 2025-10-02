package com.example.vaultofluck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.example.vaultofluck.ui.VaultOfLuckRoot
import com.example.vaultofluck.ui.theme.VaultOfLuckTheme
import com.example.vaultofluck.ui.viewmodel.VaultViewModelFactory
import com.google.accompanist.systemuicontroller.rememberSystemUiController

/**
 * Single-activity host powering the Compose navigation structure.
 */
class MainActivity : ComponentActivity() {

    private val container by lazy { VaultOfLuckApp.from(this).container }
    private val factory by lazy { VaultViewModelFactory(container) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            val preferences by container.preferences.preferences.collectAsState(
                initial = com.example.vaultofluck.data.prefs.GamePreferences(true, 0.8f, false, null)
            )
            ProvideSystemBars()
            VaultOfLuckTheme(largeText = preferences.largeText) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    VaultOfLuckRoot(factory = factory)
                }
            }
        }
    }
}

@Composable
private fun ProvideSystemBars() {
    val controller = rememberSystemUiController()
    SideEffect {
        controller.setSystemBarsColor(color = androidx.compose.ui.graphics.Color.Transparent, darkIcons = false)
    }
}
