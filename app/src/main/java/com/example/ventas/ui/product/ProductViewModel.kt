package com.example.ventas.ui.product

import android.app.Application
import androidx.lifecycle.*
import com.example.ventas.data.local.AppDatabase
import com.example.ventas.data.repo.ProductRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProductFormState(
    val name: String = "",
    val sku: String = "",
    val price: String = "", // texto -> convertimos a centavos
    val stock: String = "",
    val errors: Map<String, String> = emptyMap()
)

class ProductViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = ProductRepository(AppDatabase.get(app).productDao())

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    val products = query
        .debounce(250)
        .flatMapLatest { q -> if (q.isBlank()) repo.observeAll() else repo.search(q) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _form = MutableStateFlow(ProductFormState())
    val form: StateFlow<ProductFormState> = _form

    private var editingId: Long? = null

    fun setQuery(q: String) { _query.value = q }

    fun startCreate() {
        editingId = null
        _form.value = ProductFormState()
    }

    suspend fun loadForEdit(id: Long) {
        val p = repo.get(id) ?: return
        editingId = id
        _form.value = ProductFormState(
            name = p.name,
            sku = p.sku,
            price = "%.2f".format(p.price / 100.0),
            stock = p.stock.toString()
        )
    }

    fun onFormChange(
        name: String? = null, sku: String? = null,
        price: String? = null, stock: String? = null
    ) {
        _form.value = _form.value.copy(
            name = name ?: _form.value.name,
            sku = sku ?: _form.value.sku,
            price = price ?: _form.value.price,
            stock = stock ?: _form.value.stock
        )
    }

    private fun validate(): Boolean {
        val f = _form.value
        val errs = mutableMapOf<String,String>()
        if (f.name.isBlank()) errs["name"] = "Nombre obligatorio"
        if (f.sku.isBlank()) errs["sku"] = "SKU obligatorio"

        val priceCents = runCatching {
            val d = f.price.replace(",", ".").toDouble()
            (d * 100).toLong()
        }.getOrNull()
        if (priceCents == null || priceCents < 0) errs["price"] = "Precio inválido"

        val stock = f.stock.toIntOrNull()
        if (stock == null || stock < 0) errs["stock"] = "Stock inválido"

        _form.value = f.copy(errors = errs)
        return errs.isEmpty()
    }

    fun save(onDone: (Long?) -> Unit) = viewModelScope.launch {
        if (!validate()) return@launch
        val f = _form.value
        val priceCents = (f.price.replace(",", ".").toDouble() * 100).toLong()
        val stock = f.stock.toInt()
        val id = editingId
        if (id == null) {
            val newId = repo.create(f.name, f.sku, priceCents, stock)
            onDone(newId)
        } else {
            repo.update(id, f.name, f.sku, priceCents, stock)
            onDone(id)
        }
    }

    fun delete(id: Long) = viewModelScope.launch { repo.delete(id) }
}