// com/example/ventas/ui/sale/SaleViewModel.kt
package com.example.ventas.ui.sale

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ventas.data.local.AppDatabase
import com.example.ventas.data.repo.*
import com.example.ventas.data.local.entity.SaleWithDetails
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SaleItemForm(
    val productId: Long? = null,
    val qty: String = "1",
    val unitPriceText: String = "" // “12.50” => se convertirá a centavos
)

data class SaleFormState(
    val clientId: Long? = null,
    val paymentMethod: String = "EFECTIVO",
    val series: String = "",
    val number: String = "",
    val items: List<SaleItemForm> = listOf(SaleItemForm()),
    val errors: Map<String, String> = emptyMap()
)

class SaleViewModel(app: Application) : AndroidViewModel(app) {
    private val db = AppDatabase.get(app)
    private val repo = SaleRepository(db, db.saleDao(), db.productDao())

    val sales: StateFlow<List<SaleWithDetails>> =
        repo.observeSales().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _form = MutableStateFlow(SaleFormState())
    val form: StateFlow<SaleFormState> = _form

    fun setClient(id: Long?) { _form.value = _form.value.copy(clientId = id) }
    fun setPaymentMethod(pm: String) { _form.value = _form.value.copy(paymentMethod = pm) }
    fun setSeries(s: String) { _form.value = _form.value.copy(series = s) }
    fun setNumber(n: String) { _form.value = _form.value.copy(number = n) }

    fun addItem() { _form.value = _form.value.copy(items = _form.value.items + SaleItemForm()) }
    fun removeItem(index: Int) {
        val list = _form.value.items.toMutableList()
        if (index in list.indices && list.size > 1) {
            list.removeAt(index)
            _form.value = _form.value.copy(items = list)
        }
    }

    fun updateItem(index: Int, productId: Long? = null, qty: String? = null, unitPriceText: String? = null) {
        val list = _form.value.items.toMutableList()
        if (index !in list.indices) return
        val current = list[index]
        list[index] = current.copy(
            productId = productId ?: current.productId,
            qty = qty ?: current.qty,
            unitPriceText = unitPriceText ?: current.unitPriceText
        )
        _form.value = _form.value.copy(items = list)
    }

    private fun validate(): Boolean {
        val f = _form.value
        val errs = mutableMapOf<String, String>()

        if (f.items.isEmpty()) errs["items"] = "Agrega al menos un ítem"
        f.items.forEachIndexed { idx, it ->
            if (it.productId == null || it.productId <= 0) errs["item.$idx.productId"] = "Producto obligatorio"
            val q = it.qty.toIntOrNull()
            if (q == null || q <= 0) errs["item.$idx.qty"] = "Cantidad inválida"
            val priceCents = runCatching {
                (it.unitPriceText.replace(",", ".").toDouble() * 100).toLong()
            }.getOrNull()
            if (priceCents == null || priceCents < 0) errs["item.$idx.price"] = "Precio inválido"
        }

        _form.value = f.copy(errors = errs)
        return errs.isEmpty()
    }

    fun save(onDone: (Long?) -> Unit, onError: (String) -> Unit) = viewModelScope.launch {
        if (!validate()) return@launch
        val f = _form.value

        val items = f.items.mapIndexed { idx, it ->
            val qty = it.qty.toIntOrNull() ?: return@launch onError("Cantidad inválida en ítem ${idx + 1}")
            val unitPriceCents = runCatching {
                (it.unitPriceText.replace(",", ".").toDouble() * 100).toLong()
            }.getOrNull() ?: return@launch onError("Precio inválido en ítem ${idx + 1}")
            SaleItemRequest(
                productId = it.productId!!,
                qty = qty,
                unitPrice = unitPriceCents
            )
        }

        val req = CreateSaleRequest(
            clientId = f.clientId,
            paymentMethod = f.paymentMethod,
            series = f.series.ifBlank { null },
            number = f.number.ifBlank { null },
            items = items
        )

        try {
            val id = repo.createSale(req)
            onDone(id)
        } catch (e: IllegalArgumentException) {
            onError(e.message ?: "Error validando stock")
        } catch (e: Exception) {
            onError("Error creando venta: ${e.message}")
        }
    }
}
