// com/example/ventas/ui/screens/sale/SaleDetailScreen.kt
package com.example.ventas.ui.screens.sale

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ventas.data.local.AppDatabase
import com.example.ventas.data.local.entity.SaleWithDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaleDetailScreen(
    saleId: Long,
    onBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var data by remember { mutableStateOf<SaleWithDetails?>(null) }

    LaunchedEffect(saleId) {
        data = withContext(Dispatchers.IO) {
            AppDatabase.get(context).saleDao().getSaleWithDetails(saleId)
        }
    }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Detalle Venta #$saleId") }) }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            val s = data ?: run { Text("Cargando..."); return@Column }
            Text("ClienteId: ${s.sale.clientId ?: "-"}  •  ${s.sale.paymentMethod}")
            Text("Serie/Número: ${s.sale.series ?: "-"}-${s.sale.number ?: "-"}")
            Text("Subtotal: S/ ${"%.2f".format(s.sale.subtotal/100.0)}")
            Text("IGV: S/ ${"%.2f".format(s.sale.tax/100.0)}")
            Text("Total: S/ ${"%.2f".format(s.sale.total/100.0)}")
            Spacer(Modifier.height(12.dp))
            Text("Items:")
            s.details.forEach {
                Text("- P${it.productId}  x${it.qty}  S/ ${"%.2f".format(it.unitPrice/100.0)}  = S/ ${"%.2f".format(it.lineTotal/100.0)}")
            }
        }
    }
}
