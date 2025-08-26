package com.example.ventas.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place      // reemplazo de Map
import androidx.compose.material.icons.filled.LocationOn // reemplazo de NearMe
import androidx.compose.material.icons.filled.Receipt   // reemplazo de ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

/**
 * Modelo para los ítems del drawer
 */
private data class DrawerItem(
    val label: String,
    val icon: ImageVector,
    val route: String? = null,           // si es null => acción secundaria sin navegación
    val onClick: (() -> Unit)? = null    // para acciones como "Rate", "Donate" etc.
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNav(startDestination: String) {
    val nav = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Estado selección actual para marcar el ítem activo
    val currentDestination = nav.currentBackStackEntryAsState().value?.destination

    // Callback para abrir drawer desde pantallas internas (puedes pasarlo a tu graph)
    val onOpenDrawer: () -> Unit = { scope.launch { drawerState.open() } }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.widthIn(min = 280.dp)
            ) {
                DrawerHeader(
                    appName = "Your app idea",
                    tagline = "Amazing bootstrap",
                    accountName = "Laurent Michenaud",
                    accountEmail = "lmichen@@gmail.com"
                )

                // Usa tu AppRoute existente (NO redefinir aquí)
                val primaryItems = listOf(
                    DrawerItem("Inicio", Icons.Filled.List, AppRoute.Home.route),
                    DrawerItem("Productos", Icons.Filled.Receipt, AppRoute.ProductList.route),
                    DrawerItem("Clientes", Icons.Filled.LocationOn, AppRoute.ClientList.route),
                    DrawerItem("Ventas (lista)", Icons.Filled.Place, AppRoute.SaleList.route),
                    DrawerItem("Nueva venta", Icons.Filled.LocationOn, AppRoute.SaleNew.route)
                )
                val secondaryItems = listOf(
                    DrawerItem("Settings", Icons.Filled.Settings, /* si tienes ruta */ null),
                    DrawerItem("Rate this app", Icons.Filled.StarRate, onClick = { /* TODO rate */ }),
                    DrawerItem("Donate", Icons.Filled.Favorite, onClick = { /* TODO donate */ }),
                    DrawerItem("ChangeLog", Icons.Filled.Info, onClick = { /* TODO changelog */ }),
                    DrawerItem("Ayuda", Icons.Filled.Help, onClick = { /* TODO help */ })
                )

                // Lista scrollable del contenido del drawer
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 12.dp)
                ) {
                    primaryItems.forEach { item ->
                        DrawerNavItem(
                            item = item,
                            currentDestination = currentDestination,
                            onNavigate = { route ->
                                scope.launch {
                                    nav.navigate(route) {
                                        popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                    drawerState.close()
                                }
                            }
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    secondaryItems.forEach { item ->
                        if (item.route != null) {
                            DrawerNavItem(
                                item = item,
                                currentDestination = currentDestination,
                                onNavigate = { route ->
                                    scope.launch {
                                        nav.navigate(route) {
                                            popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                        drawerState.close()
                                    }
                                }
                            )
                        } else {
                            NavigationDrawerItem(
                                label = { Text(item.label) },
                                selected = false,
                                onClick = {
                                    item.onClick?.invoke()
                                    scope.launch { drawerState.close() }
                                },
                                icon = { Icon(item.icon, contentDescription = null) },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    NavigationDrawerItem(
                        label = { Text("Logout") },
                        selected = false,
                        onClick = { /* TODO logout */ scope.launch { drawerState.close() } },
                        icon = { Icon(Icons.Filled.ExitToApp, contentDescription = null) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                val currentDestination = nav.currentBackStackEntryAsState().value?.destination
                val currentTitle = getTitleForDestination(currentDestination?.route)
                TopAppBar(
                    title = { Text(currentTitle) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors()
                )
            }
        ) { padding ->
            NavHost(
                navController = nav,
                startDestination = startDestination,
                modifier = Modifier.padding(padding)
            ) {
                // Tu grafo de navegación real
                appGraph(nav, onOpenDrawer)
            }
        }
    }
}

@Composable
private fun DrawerNavItem(
    item: DrawerItem,
    currentDestination: NavDestination?,
    onNavigate: (String) -> Unit
) {
    val selected = isSelected(currentDestination, item.route)
    NavigationDrawerItem(
        label = { Text(item.label) },
        selected = selected,
        onClick = { item.route?.let(onNavigate) },
        icon = { Icon(item.icon, contentDescription = null) },
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    )
}

/**
 * Encabezado tipo "cuenta".
 */
@Composable
private fun DrawerHeader(
    appName: String,
    tagline: String,
    accountName: String,
    accountEmail: String,
) {
    val primary = MaterialTheme.colorScheme.primary
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(primary)
            .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 16.dp)
            .statusBarsPadding()
    ) {
        Text(
            text = appName,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            text = tagline,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
        )

        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = accountName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = accountEmail,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                )
            }

            Icon(
                imageVector = Icons.Filled.Circle, // puedes cambiar por ExpandMore si usas icons-extended
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                modifier = Modifier.size(10.dp)
            )
        }
    }
}

/**
 * Marca seleccionado incluso cuando la ruta actual tiene parámetros.
 * Ej: "sale_detail/{id}" vs "sale_detail/15"
 */
private fun isSelected(currentDestination: NavDestination?, itemRoute: String?): Boolean {
    if (currentDestination == null || itemRoute == null) return false
    val current = currentDestination.route ?: return false
    // si la ruta del item tiene {param}, comparamos hasta antes del primer "/{"
    val base = itemRoute.substringBefore("/{")
    return current == itemRoute || current.startsWith(base)
}
private fun getTitleForDestination(route: String?): String {
    return when (route) {
        AppRoute.Home.route -> "Inicio"
        AppRoute.ProductList.route -> "Productos"
        AppRoute.ClientList.route -> "Clientes"
        AppRoute.SaleList.route -> "Ventas"
        AppRoute.SaleNew.route -> "Nueva venta"
        else -> "Ventas" // fallback por defecto
    }
}