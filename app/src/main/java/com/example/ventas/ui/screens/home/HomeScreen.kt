package com.example.ventas.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.ventas.ui.screens.product.ProductListScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenDrawer: () -> Unit,
    onNewProduct: () -> Unit,
    onEditProduct: (Long) -> Unit
) {
    var selectedTab by rememberSaveable  { mutableStateOf(0) } // ← guarda estado

    val tabs = listOf("Inicio", "Productos")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Ventas") },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "menu")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { i, t ->
                    Tab(
                        selected = selectedTab == i,
                        onClick = { selectedTab = i },
                        text = { Text(t) }
                    )
                }
            }

            when (selectedTab) {
                0 -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Dashboard simple (Inicio)")
                }
                1 -> {
                    // 👇 Renderiza la misma pantalla de productos pero inline, sin navigate()
                    ProductListScreen(
                        onNew = onNewProduct,
                        onEdit = onEditProduct,
                        onOpenDrawer = onOpenDrawer
                    )
                }
            }
        }
    }
}
