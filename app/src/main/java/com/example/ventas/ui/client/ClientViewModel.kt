// com/example/ventas/ui/client/ClientViewModel.kt
package com.example.ventas.ui.client

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ventas.data.local.AppDatabase
import com.example.ventas.data.repo.ClientRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ClientFormState(
    val fullName: String = "",
    val document: String = "",
    val phone: String = "",
    val email: String = "",
    val address: String = "",
    val errors: Map<String, String> = emptyMap()
)

class ClientViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = ClientRepository(AppDatabase.get(app).clientDao())

    // 🔹 Query para pantallas con buscador (lista de clientes)
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    // 🔹 Lista filtrada para pantallas con búsqueda (ClientListScreen)
    val clients = query
        .debounce(250)
        .flatMapLatest { q -> if (q.isBlank()) repo.observeAll() else repo.search(q) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 🔹 Lista “siempre” (sin búsqueda) para selectores (ClientSelector en SaleForm)
    val clientsAll = repo.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _form = MutableStateFlow(ClientFormState())
    val form: StateFlow<ClientFormState> = _form

    private var editingId: Long? = null

    fun setQuery(q: String) { _query.value = q }

    fun startCreate() {
        editingId = null
        _form.value = ClientFormState()
    }

    suspend fun loadForEdit(id: Long) {
        val c = repo.get(id) ?: return
        editingId = id
        _form.value = ClientFormState(
            fullName = c.fullName,
            document = c.document,
            phone = c.phone.orEmpty(),
            email = c.email.orEmpty(),
            address = c.address.orEmpty()
        )
    }

    fun onFormChange(
        fullName: String? = null,
        document: String? = null,
        phone: String? = null,
        email: String? = null,
        address: String? = null
    ) {
        _form.value = _form.value.copy(
            fullName = fullName ?: _form.value.fullName,
            document = document ?: _form.value.document,
            phone = phone ?: _form.value.phone,
            email = email ?: _form.value.email,
            address = address ?: _form.value.address
        )
    }

    private fun validate(): Boolean {
        val f = _form.value
        val errs = mutableMapOf<String, String>()
        if (f.fullName.isBlank()) errs["fullName"] = "Nombre obligatorio"
        if (f.document.isBlank()) errs["document"] = "Documento obligatorio"
        if (f.email.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(f.email).matches()) {
            errs["email"] = "Email inválido"
        }
        _form.value = f.copy(errors = errs)
        return errs.isEmpty()
    }

    fun save(onDone: (Long?) -> Unit) = viewModelScope.launch {
        if (!validate()) return@launch
        val f = _form.value
        val id = editingId
        if (id == null) {
            val newId = repo.create(
                f.fullName, f.document,
                f.phone.ifBlank { null },
                f.email.ifBlank { null },
                f.address.ifBlank { null }
            )
            onDone(newId)
        } else {
            repo.update(
                id, f.fullName, f.document,
                f.phone.ifBlank { null },
                f.email.ifBlank { null },
                f.address.ifBlank { null }
            )
            onDone(id)
        }
    }

    fun delete(id: Long) = viewModelScope.launch { repo.delete(id) }
}
