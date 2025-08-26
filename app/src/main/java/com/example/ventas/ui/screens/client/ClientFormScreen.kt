// com/example/ventas/ui/screens/client/ClientFormScreen.kt
package com.example.ventas.ui.screens.client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ventas.ui.client.ClientViewModel
import com.example.ventas.ui.client.rememberClientVM
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientFormScreen(
    clientId: Long?,                 // null => Nuevo
    onSaved: (Long?) -> Unit,
    onCancel: () -> Unit = {},
    vm: ClientViewModel = rememberClientVM()
) {
    val scope = rememberCoroutineScope()
    val form by vm.form.collectAsState()

    LaunchedEffect(clientId) {
        if (clientId != null && clientId > 0) vm.loadForEdit(clientId) else vm.startCreate()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (clientId == null || clientId <= 0) "Nuevo cliente" else "Editar cliente") }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {

            OutlinedTextField(
                value = form.fullName,
                onValueChange = { vm.onFormChange(fullName = it) },
                isError = form.errors.containsKey("fullName"),
                label = { Text("Nombre completo / Razón social") },
                supportingText = { Text(form.errors["fullName"] ?: "") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = form.document,
                onValueChange = { vm.onFormChange(document = it) },
                isError = form.errors.containsKey("document"),
                label = { Text("Documento (DNI/RUC)") },
                supportingText = { Text(form.errors["document"] ?: "") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = form.phone,
                onValueChange = { vm.onFormChange(phone = it) },
                label = { Text("Teléfono (opcional)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = form.email,
                onValueChange = { vm.onFormChange(email = it) },
                isError = form.errors.containsKey("email"),
                label = { Text("Email (opcional)") },
                supportingText = { Text(form.errors["email"] ?: "") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = form.address,
                onValueChange = { vm.onFormChange(address = it) },
                label = { Text("Dirección (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

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
