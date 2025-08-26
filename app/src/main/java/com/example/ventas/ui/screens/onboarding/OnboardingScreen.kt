package com.example.ventas.ui.screens.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ventas.data.datastore.UserPrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val pager = rememberPagerState(pageCount = { 3 })
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        HorizontalPager(state = pager, modifier = Modifier.weight(1f)) { page ->
            when (page) {
                0 -> Text("Bienvenido a Ventas", style = MaterialTheme.typography.headlineMedium)
                1 -> Text("Administra tus productos y stock")
                2 -> Text("¡Listo para comenzar! 🚀")
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(enabled = pager.currentPage > 0,
                onClick = { scope.launch { pager.animateScrollToPage(pager.currentPage - 1) } }
            ) { Text("Atrás") }
            if (pager.currentPage < 2) {
                Button(onClick = { scope.launch { pager.animateScrollToPage(pager.currentPage + 1) } }) {
                    Text("Siguiente")
                }
            } else {
                Button(onClick = {
                    scope.launch(Dispatchers.IO) {
                        UserPrefs.setOnboardDone(ctx, true)
                        withContext(Dispatchers.Main) { onFinish() }
                    }
                }) { Text("Empezar") }
            }
        }
    }
}
