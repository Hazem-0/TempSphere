package com.darkzoom.tempsphere.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.darkzoom.tempsphere.ui.common.components.AfterNoonBackground
import com.darkzoom.tempsphere.ui.common.components.MorningBackground
import com.darkzoom.tempsphere.ui.common.components.NightBackground
import com.darkzoom.tempsphere.ui.settings.SettingsScreen
import com.darkzoom.tempsphere.ui.theme.TempSphereTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )

        setContent {
            TempSphereTheme(darkTheme = true) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AfterNoonBackground()
                    SettingsScreen()
                }
            }
        }
    }
}