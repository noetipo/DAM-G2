package com.example.ventas.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

@Composable
fun AppNav(startDestination: String) {
    val nav = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Callback que podrán usar las pantallas para abrir el drawer
    val onOpenDrawer: () -> Unit = { scope.launch { drawerState.open() } }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Menú", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)

                NavigationDrawerItem(
                    label = { Text("Inicio") },
                    selected = false,
                    onClick = {
                        nav.navigate(AppRoute.Home.route) {
                            popUpTo(0)
                            launchSingleTop = true
                        }
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Productos") },
                    selected = false,
                    onClick = {
                        nav.navigate(AppRoute.ProductList.route) { launchSingleTop = true }
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Clientes") },
                    selected = false,
                    onClick = {
                        nav.navigate(AppRoute.ClientList.route) { launchSingleTop = true }
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("ventas") },
                    selected = false,
                    onClick = {
                        nav.navigate(AppRoute.SaleList.route) { launchSingleTop = true }
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold { padding ->
            NavHost(
                navController = nav,
                startDestination = startDestination,
                modifier = Modifier.padding(padding)
            ) {
                appGraph(nav, onOpenDrawer)
            }
        }
    }
}