package com.example.ventas.ui.screens.product

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ventas.ui.product.ProductViewModel
import com.example.ventas.ui.product.rememberProductVM
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreen(
    productId: Long?,                  // null => Nuevo
    onSaved: (Long?) -> Unit,
    onCancel: () -> Unit = {},         // 👈 nuevo callback (opcional)
    vm: ProductViewModel = rememberProductVM()
) {
    val scope = rememberCoroutineScope()
    val form by vm.form.collectAsState()

    LaunchedEffect(productId) {
        if (productId != null && productId > 0) vm.loadForEdit(productId) else vm.startCreate()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (productId == null || productId <= 0) "Nuevo producto" else "Editar producto") }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = form.name,
                onValueChange = { vm.onFormChange(name = it) },
                isError = form.errors.containsKey("name"),
                label = { Text("Nombre") },
                supportingText = { Text(form.errors["name"] ?: "") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = form.sku,
                onValueChange = { vm.onFormChange(sku = it) },
                isError = form.errors.containsKey("sku"),
                label = { Text("SKU") },
                supportingText = { Text(form.errors["sku"] ?: "") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = form.price,
                onValueChange = { vm.onFormChange(price = it) },
                isError = form.errors.containsKey("price"),
                label = { Text("Precio (S/)") },
                supportingText = { Text(form.errors["price"] ?: "") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = form.stock,
                onValueChange = { vm.onFormChange(stock = it) },
                isError = form.errors.containsKey("stock"),
                label = { Text("Stock") },
                supportingText = { Text(form.errors["stock"] ?: "") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // 👇 Botones: Cancelar (Outlined) + Guardar (Filled)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) { Text("Cancelar") }

                Button(
                    onClick = { scope.launch { vm.save(onDone = onSaved) } },
                    modifier = Modifier.weight(1f)
                ) { Text("Guardar") }
            }
        }
    }
}
