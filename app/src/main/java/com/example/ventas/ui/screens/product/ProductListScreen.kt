package com.example.ventas.ui.screens.product

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu      // 👈 importa el icono de menú
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ventas.ui.product.ProductViewModel
import com.example.ventas.ui.product.rememberProductVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    onNew: () -> Unit,
    onEdit: (Long) -> Unit,
    onOpenDrawer: () -> Unit = {},                 // 👈 nuevo parámetro con default
    vm: ProductViewModel = rememberProductVM()
) {
    val products by vm.products.collectAsState()
    var query by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Productos") },
                /*navigationIcon = {                     // 👇 botón hamburguesa
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Filled.Menu, contentDescription = "Abrir menú")
                    }
                }*/
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { vm.startCreate(); onNew() }) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo")
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it; vm.setQuery(it) },
                label = { Text("Buscar por nombre o SKU") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            LazyColumn {
                items(products, key = { it.id }) { p ->
                    ElevatedCard(Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                        ListItem(
                            headlineContent = { Text(p.name) },
                            supportingContent = {
                                Text("SKU: ${p.sku}  •  S/ ${"%.2f".format(p.price / 100.0)}  •  Stock: ${p.stock}")
                            },
                            trailingContent = {
                                Row {
                                    IconButton(onClick = { onEdit(p.id) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                                    }
                                    IconButton(onClick = { vm.delete(p.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
