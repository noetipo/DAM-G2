// com/example/ventas/ui/components/ClientSelector.kt
package com.example.ventas.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.example.ventas.data.local.entity.Client

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientSelector(
    selectedClientId: Long?,
    clients: List<Client>,
    onSelected: (Long?) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Cliente"
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = clients.find { it.id == selectedClientId }
    val display = selected?.let { "${it.fullName} • ${it.document}" } ?: ""

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            readOnly = true,
            value = if (display.isBlank()) "Seleccione cliente (opcional)" else display,
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
            // Opción “sin cliente” (venta mostrador)
            DropdownMenuItem(
                text = { Text("— Sin cliente / Mostrador —") },
                onClick = {
                    onSelected(null)
                    expanded = false
                }
            )
            clients.forEach { c ->
                DropdownMenuItem(
                    text = {
                        Text(
                            "${c.fullName} • ${c.document}",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    onClick = {
                        onSelected(c.id)
                        expanded = false
                    }
                )
            }
        }
    }
}
