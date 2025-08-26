// com/example/ventas/ui/screens/sale/SaleFormScreen.kt
package com.example.ventas.ui.screens.sale

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ventas.ui.sale.rememberSaleVM
import com.example.ventas.ui.product.rememberProductVM
import com.example.ventas.ui.client.rememberClientVM
import com.example.ventas.ui.components.ProductSelector
import com.example.ventas.ui.components.ClientSelector
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaleFormScreen(
    onSaved: (Long?) -> Unit,
    onCancel: () -> Unit = {}
) {
    val vm = rememberSaleVM()
    val form by vm.form.collectAsState()

    // Productos para selector
    val productVM = rememberProductVM()
    val products by productVM.products.collectAsState()

    // Clientes para selector
    val clientVM = rememberClientVM()
    val clients by clientVM.clients.collectAsState(initial = emptyList()) // expón un flow/list en tu VM

    val scope = rememberCoroutineScope()
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Nueva Venta") }) }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {

            // ==== CLIENTE (select) ====
            ClientSelector(
                selectedClientId = form.clientId,
                clients = clients,
                onSelected = { id -> vm.setClient(id) },
                label = "Cliente"
            )
            Spacer(Modifier.height(12.dp))

            // Serie / Número (si decides mantenerlos manuales)
            OutlinedTextField(
                value = form.series,
                onValueChange = { vm.setSeries(it) },
                label = { Text("Serie (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = form.number,
                onValueChange = { vm.setNumber(it) },
                label = { Text("Número (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            // ===== ÍTEMS =====
            form.items.forEachIndexed { idx, it ->
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Ítem ${idx + 1}", style = MaterialTheme.typography.titleSmall)
                        Spacer(Modifier.height(8.dp))

                        // --- SELECTOR DE PRODUCTO ---
                        ProductSelector(
                            selectedProductId = it.productId,
                            products = products,
                            onSelected = { productId ->
                                val selected = products.find { p -> p.id == productId }
                                val priceText = selected?.let { p -> "%.2f".format(p.price / 100.0) } ?: ""
                                vm.updateItem(idx, productId = productId, unitPriceText = priceText)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = "Producto"
                        )
                        if (form.errors.containsKey("item.$idx.productId")) {
                            Text(
                                text = form.errors["item.$idx.productId"] ?: "",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        // Cantidad
                        OutlinedTextField(
                            value = it.qty,
                            onValueChange = { s -> vm.updateItem(idx, qty = s) },
                            label = { Text("Cantidad") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = form.errors.containsKey("item.$idx.qty"),
                            supportingText = { Text(form.errors["item.$idx.qty"] ?: "") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))

                        // Precio Unitario
                        OutlinedTextField(
                            value = it.unitPriceText,
                            onValueChange = { s -> vm.updateItem(idx, unitPriceText = s) },
                            label = { Text("Precio Unitario (S/)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            isError = form.errors.containsKey("item.$idx.price"),
                            supportingText = { Text(form.errors["item.$idx.price"] ?: "") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            if (form.items.size > 1) {
                                OutlinedButton(onClick = { vm.removeItem(idx) }) { Text("Quitar ítem") }
                            }
                            OutlinedButton(onClick = { vm.addItem() }) { Text("Añadir ítem") }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            error?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) { Text("Cancelar") }
                Button(
                    onClick = {
                        scope.launch {
                            vm.save(
                                onDone = onSaved,
                                onError = { msg -> error = msg }
                            )
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Guardar") }
            }
        }
    }
}
