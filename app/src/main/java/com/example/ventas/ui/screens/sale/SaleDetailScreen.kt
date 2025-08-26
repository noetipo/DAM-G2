// com/example/ventas/ui/screens/sale/SaleDetailScreen.kt
package com.example.ventas.ui.screens.sale

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ventas.data.local.AppDatabase
import com.example.ventas.data.local.entity.SaleWithClientAndDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaleDetailScreen(
    saleId: Long,
    onBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var data by remember { mutableStateOf<SaleWithClientAndDetails?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    fun load() {
        scope.launch {
            isLoading = true
            error = null
            data = try {
                withContext(Dispatchers.IO) {
                    AppDatabase.get(context).saleDao().getSaleWithClientAndDetails(saleId)
                }
            } catch (e: Exception) {
                error = "No se pudo cargar la venta: ${e.message}"
                null
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(saleId) { load() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detalle Venta #$saleId") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { load() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Recargar")
                    }
                }
            )
        }
    ) { padding ->
        when {
            isLoading -> {
                Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(error!!, color = MaterialTheme.colorScheme.error)
                }
            }
            data == null -> {
                Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Venta no encontrada")
                }
            }
            else -> {
                val s = data!!
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Cabecera
                    item {
                        ElevatedCard(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                val serieNumero = "${s.sale.series ?: "-"}-${s.sale.number ?: "-"}"
                                Text(
                                    text = "Comprobante: $serieNumero",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )

                                val fecha = s.sale.issueAt.toDateString()
                                val cliente = s.client?.fullName ?: "Mostrador"
                                val doc = s.client?.document?.let { " • $it" }.orEmpty()

                                Text("Fecha: $fecha")
                                Text("Cliente: $cliente$doc")
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    AssistChip(
                                        onClick = {},
                                        label = { Text(s.sale.paymentMethod) }
                                    )
                                    StatusChip(status = s.sale.status)
                                }
                            }
                        }
                    }

                    // Items
                    item {
                        Text(
                            text = "Ítems (${s.details.size})",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                        )
                    }
                    items(s.details, key = { it.id }) { d ->
                        ElevatedCard(Modifier.fillMaxWidth()) {
                            Row(
                                Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(Modifier.weight(1f)) {
                                    // Si quieres mostrar nombre del producto, necesitarías relacionarlo;
                                    // por ahora mostramos el ID:
                                    Text("Producto ID: ${d.productId}", fontWeight = FontWeight.Medium)
                                    Text("Cant: ${d.qty}")
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("P. Unit: S/ ${d.unitPrice.asMoney()}")
                                    Text("Importe: S/ ${d.lineTotal.asMoney()}", fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }

                    // Totales
                    item {
                        ElevatedCard(Modifier.fillMaxWidth()) {
                            Column(
                                Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Subtotal")
                                    Text("S/ ${s.sale.subtotal.asMoney()}")
                                }
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("IGV")
                                    Text("S/ ${s.sale.tax.asMoney()}")
                                }
                                Divider(Modifier.padding(vertical = 6.dp))
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Total", fontWeight = FontWeight.Bold)
                                    Text("S/ ${s.sale.total.asMoney()}", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/* -------------------- Helpers UI -------------------- */

@Composable
private fun StatusChip(status: String) {
    val label = status.uppercase(Locale.getDefault())
    val colors = when (label) {
        "EMITIDO" -> AssistChipDefaults.assistChipColors()
        "ANULADO" -> AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            labelColor = MaterialTheme.colorScheme.onErrorContainer
        )
        else -> AssistChipDefaults.assistChipColors()
    }
    AssistChip(onClick = {}, label = { Text(label) }, colors = colors)
}

private fun Long.asMoney(): String = "%.2f".format(this / 100.0)

private fun Long.toDateString(): String {
    // Formato: 2025-08-26 14:35
    return try {
        val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        fmt.format(Date(this))
    } catch (_: Throwable) {
        this.toString()
    }
}
