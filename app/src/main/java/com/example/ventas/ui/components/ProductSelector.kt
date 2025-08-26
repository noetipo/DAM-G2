// com/example/ventas/ui/components/ProductSelector.kt
package com.example.ventas.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import com.example.ventas.data.local.entity.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductSelector(
    selectedProductId: Long?,
    products: List<Product>,
    onSelected: (Long?) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Producto"
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedProduct = products.find { it.id == selectedProductId }
    val displayText = selectedProduct?.let { "${it.name} • S/ ${"%.2f".format(it.price / 100.0)}" } ?: ""

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            readOnly = true,
            value = if (displayText.isBlank()) "Seleccione producto" else displayText,
            onValueChange = {},
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            products.forEach { p ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "${p.name} • S/ ${"%.2f".format(p.price / 100.0)}",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    onClick = {
                        onSelected(p.id)
                        expanded = false
                    }
                )
            }
        }
    }
}
