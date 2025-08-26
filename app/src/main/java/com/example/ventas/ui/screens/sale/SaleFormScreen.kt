// com/example/ventas/ui/screens/sale/SaleFormScreen.kt
package com.example.ventas.ui.screens.sale

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ventas.ui.sale.SaleItemForm
import com.example.ventas.ui.sale.rememberSaleVM
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaleFormScreen(
    onSaved: (Long?) -> Unit,
    onCancel: () -> Unit = {}
) {
    val vm = rememberSaleVM()
    val form by vm.form.collectAsState()
    val scope = rememberCoroutineScope()
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Nueva Venta") }) }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
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
            Spacer(Modifier.height(8.dp))

            // Lista muy simple de ítems (por ahora manual)
            form.items.forEachIndexed { idx, it ->
                Text("Ítem ${idx + 1}")
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = it.productId?.toString() ?: "",
                    onValueChange = { s -> vm.updateItem(idx, productId = s.toLongOrNull()) },
                    label = { Text("ProductId") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = form.errors.containsKey("item.$idx.productId"),
                    supportingText = { Text(form.errors["item.$idx.productId"] ?: "") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = it.qty,
                    onValueChange = { s -> vm.updateItem(idx, qty = s) },
                    label = { Text("Cantidad") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = form.errors.containsKey("item.$idx.qty"),
                    supportingText = { Text(form.errors["item.$idx.qty"] ?: "") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(4.dp))
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
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = { vm.addItem() }) { Text("Añadir ítem") }
                if (form.items.size > 1) {
                    OutlinedButton(onClick = { vm.removeItem(form.items.lastIndex) }) { Text("Quitar último") }
                }
            }

            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
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
