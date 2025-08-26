package com.example.ventas.data.repo

import com.example.ventas.data.local.dao.ClientDao
import com.example.ventas.data.local.entity.Client
import kotlinx.coroutines.flow.Flow

class ClientRepository(private val dao: ClientDao) {
    fun observeAll(): Flow<List<Client>> = dao.observeAll()
    fun search(q: String): Flow<List<Client>> = dao.search(q)
    suspend fun get(id: Long) = dao.findById(id)

    suspend fun create(fullName: String, document: String, phone: String?, email: String?, address: String?): Long {
        val c = Client(
            fullName = fullName.trim(),
            document = document.trim(),
            phone = phone?.trim(),
            email = email?.trim(),
            address = address?.trim()
        )
        return dao.insert(c)
    }

    suspend fun update(id: Long, fullName: String, document: String, phone: String?, email: String?, address: String?) {
        val current = dao.findById(id) ?: return
        dao.update(current.copy(
            fullName = fullName.trim(),
            document = document.trim(),
            phone = phone?.trim(),
            email = email?.trim(),
            address = address?.trim(),
            updatedAt = System.currentTimeMillis()
        ))
    }

    suspend fun delete(id: Long) = dao.softDelete(id)
}
