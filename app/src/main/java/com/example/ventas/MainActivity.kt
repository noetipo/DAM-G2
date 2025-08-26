package com.example.ventas
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ventas.data.datastore.UserPrefs
import com.example.ventas.navigation.AppNav
import com.example.ventas.navigation.AppRoute
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val done by UserPrefs.onboardDoneFlow(this).collectAsState(initial = false)
            val start = if (done) AppRoute.Home.route else AppRoute.Onboarding.route
            MaterialTheme { AppNav(start) }
        }
    }
}