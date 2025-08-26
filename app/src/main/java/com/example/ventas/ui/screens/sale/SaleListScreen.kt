// com/example/ventas/ui/screens/sale/SaleListScreen.kt
package com.example.ventas.ui.screens.sale

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ventas.ui.sale.rememberSaleVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaleListScreen(
    onNew: () -> Unit,
    onOpenDetail: (Long) -> Unit,
    onOpenDrawer: () -> Unit = {}
) {
    val vm = rememberSaleVM()
    val sales by vm.sales.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Ventas") },
                /*navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Filled.Menu, contentDescription = "Abrir menú")
                    }
                }*/
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNew) {
                Icon(Icons.Default.Add, contentDescription = "Nueva venta")
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            LazyColumn {
                items(sales, key = { it.sale.id }) { s ->
                    /*ElevatedCard(Modifier.fillMaxWidth().padding(bottom = 8.dp), onClick = { onOpenDetail(s.sale.id) }) {
                        ListItem(
                            headlineContent = { Text("Venta #${s.sale.id}  •  Total S/ ${"%.2f".format(s.sale.total / 100.0)}") },
                            supportingContent = {
                                Text("ClienteId: ${s.sale.clientId ?: "-"}  •  ${s.details.size} ítems  •  ${s.sale.paymentMethod}")
                            }
                        )
                    }*/

                    ElevatedCard(
                        onClick = { onOpenDetail(s.sale.id) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    ) {
                        ListItem(
                            headlineContent = { Text("Venta #${s.sale.id}  •  Total S/ ${"%.2f".format(s.sale.total / 100.0)}") },
                            supportingContent = {
                                Text("ClienteId: ${s.sale.clientId ?: "-"}  •  ${s.details.size} ítems  •  ${s.sale.paymentMethod}")
                            }
                        )
                    }
                }
            }
        }
    }
}
