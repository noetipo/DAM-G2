// com/example/ventas/ui/screens/sale/SaleFormScreen.kt
package com.example.ventas.ui.screens.sale

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
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

    // Productos para selector (usa initial vacío por seguridad)
    val productVM = rememberProductVM()
    val products by productVM.products.collectAsState(initial = emptyList())

    // Clientes para selector (expone un flow/list en tu VM)
    val clientVM = rememberClientVM()
    val clients by clientVM.clients.collectAsState(initial = emptyList())

    val scope = rememberCoroutineScope()
    var error by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Nueva Venta") }) }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .imePadding() // evita que el teclado tape campos/botones
                .navigationBarsPadding()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
        ) {
            // ===== CABECERA: Cliente + Serie/Número =====
            item {
                ClientSelector(
                    selectedClientId = form.clientId,
                    clients = clients,
                    onSelected = { id -> vm.setClient(id) },
                    label = "Cliente"
                )
                if (form.errors.containsKey("clientId")) {
                    Text(
                        text = form.errors["clientId"].orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            /*item {
                OutlinedTextField(
                    value = form.series,
                    onValueChange = { vm.setSeries(it) },
                    label = { Text("Serie (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = form.number,
                    onValueChange = { vm.setNumber(it) },
                    label = { Text("Número (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }*/

            // ===== ÍTEMS DINÁMICOS =====
            itemsIndexed(form.items) { idx, it ->
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
                                text = form.errors["item.$idx.productId"].orEmpty(),
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
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = form.errors.containsKey("item.$idx.qty"),
                            supportingText = {
                                if (form.errors.containsKey("item.$idx.qty")) {
                                    Text(form.errors["item.$idx.qty"].orEmpty())
                                }
                            },
                            singleLine = true
                        )

                        Spacer(Modifier.height(8.dp))

                        // Precio Unitario
                        OutlinedTextField(
                            value = it.unitPriceText,
                            onValueChange = { s -> vm.updateItem(idx, unitPriceText = s) },
                            label = { Text("Precio Unitario (S/)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            isError = form.errors.containsKey("item.$idx.price"),
                            supportingText = {
                                if (form.errors.containsKey("item.$idx.price")) {
                                    Text(form.errors["item.$idx.price"].orEmpty())
                                }
                            },
                            singleLine = true
                        )

                        Spacer(Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            if (form.items.size > 1) {
                                OutlinedButton(onClick = {
                                    focusManager.clearFocus()
                                    vm.removeItem(idx)
                                }) {
                                    Text("Quitar ítem")
                                }
                            }
                            OutlinedButton(onClick = {
                                focusManager.clearFocus()
                                vm.addItem()
                            }) {
                                Text("Añadir ítem")
                            }
                        }
                    }
                }
            }

            // ===== MENSAJE DE ERROR GLOBAL (si existe) =====
            if (error != null) {
                item {
                    Text(
                        text = error.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // ===== ACCIONES =====
            item {
                Spacer(Modifier.height(8.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            focusManager.clearFocus()
                            onCancel()
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("Cancelar") }

                    Button(
                        onClick = {
                            focusManager.clearFocus()
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
}
