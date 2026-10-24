package com.dev.wine.music.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.dev.wine.music.app.ui.navigation.MainScreen
import com.dev.wine.music.app.ui.theme.WineMusicAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        splashDelay()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WineMusicAppTheme {
                MainScreen()
            }
        }
    }

    private fun splashDelay() {
        var keepSplash = true
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { keepSplash }
        lifecycleScope.launch {
            delay(2000)
            keepSplash = false
        }
    }
}
