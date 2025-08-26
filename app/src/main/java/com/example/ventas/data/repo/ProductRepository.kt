package com.example.ventas.data.repo

import com.example.ventas.data.local.dao.ProductDao
import com.example.ventas.data.local.entity.Product
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val dao: ProductDao) {
    fun observeAll(): Flow<List<Product>> = dao.observeAll()
    fun search(q: String): Flow<List<Product>> = dao.search(q)
    suspend fun get(id: Long) = dao.findById(id)

    suspend fun create(name: String, sku: String, priceCents: Long, stock: Int): Long {
        val p = Product(
            name = name.trim(),
            sku = sku.trim(),
            price = priceCents,
            stock = stock
        )
        return dao.insert(p)
    }

    suspend fun update(id: Long, name: String, sku: String, priceCents: Long, stock: Int) {
        val current = dao.findById(id) ?: return
        dao.update(current.copy(
            name = name.trim(),
            sku = sku.trim(),
            price = priceCents,
            stock = stock,
            updatedAt = System.currentTimeMillis()
        ))
    }

    suspend fun delete(id: Long) = dao.softDelete(id)
}