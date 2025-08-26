// com/example/ventas/ui/screens/client/ClientListScreen.kt
package com.example.ventas.ui.screens.client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ventas.ui.client.ClientViewModel
import com.example.ventas.ui.client.rememberClientVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientListScreen(
    onNew: () -> Unit,
    onEdit: (Long) -> Unit,
    onOpenDrawer: () -> Unit = {},
    vm: ClientViewModel = rememberClientVM()
) {
    val clients by vm.clients.collectAsState()
    var query by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Clientes") },
                /*navigationIcon = {
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
                label = { Text("Buscar por nombre o documento") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            LazyColumn {
                items(clients, key = { it.id }) { c ->
                    ElevatedCard(Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                        ListItem(
                            headlineContent = { Text(c.fullName) },
                            supportingContent = {
                                val line2 = buildString {
                                    append("Doc: ${c.document}")
                                    if (!c.phone.isNullOrBlank()) append("  •  Tel: ${c.phone}")
                                    if (!c.email.isNullOrBlank()) append("  •  ${c.email}")
                                }
                                Text(line2)
                            },
                            trailingContent = {
                                Row {
                                    IconButton(onClick = { onEdit(c.id) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                                    }
                                    IconButton(onClick = { vm.delete(c.id) }) {
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
