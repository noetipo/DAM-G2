// com/example/ventas/data/repo/SaleRepository.kt
package com.example.ventas.data.repo

import androidx.room.withTransaction
import com.example.ventas.data.local.AppDatabase
import com.example.ventas.data.local.dao.ProductDao
import com.example.ventas.data.local.dao.SaleDao
import com.example.ventas.data.local.entity.Sale
import com.example.ventas.data.local.entity.SaleDetail
import kotlinx.coroutines.flow.Flow
import com.example.ventas.data.local.entity.SaleWithDetails

class SaleRepository(
    private val db: AppDatabase,
    private val saleDao: SaleDao,
    private val productDao: ProductDao
) {

    fun observeSales(): Flow<List<SaleWithDetails>> = saleDao.observeSales()

    /**
     * Crea la venta calculando subtotal/IGV/total y descontando stock.
     * Lanza IllegalArgumentException si stock insuficiente o datos inválidos.
     */
    suspend fun createSale(req: CreateSaleRequest): Long {
        require(req.items.isNotEmpty()) { "Debe incluir al menos un ítem" }
        require(req.items.all { it.qty > 0 && it.unitPrice >= 0 }) { "Cantidades y precios inválidos" }

        // Cálculo de montos
        val subtotal = req.items.fold(0L) { acc, it -> acc + it.unitPrice * it.qty }
        val tax = Math.round(subtotal * req.taxRate).toLong()
        val total = subtotal + tax

        val now = System.currentTimeMillis()

        return db.withTransaction {
            // 1) Insertar venta
            val saleId = saleDao.insertSale(
                Sale(
                    clientId = req.clientId,
                    paymentMethod = req.paymentMethod,
                    series = req.series,
                    number = req.number,
                    subtotal = subtotal,
                    tax = tax,
                    total = total,
                    createdAt = now,
                    updatedAt = now
                )
            )

            // 2) Insertar detalles
            val details = req.items.map { i ->
                SaleDetail(
                    saleId = saleId,
                    productId = i.productId,
                    qty = i.qty,
                    unitPrice = i.unitPrice,
                    lineTotal = i.unitPrice * i.qty,
                    createdAt = now,
                    updatedAt = now
                )
            }
            saleDao.insertDetails(details)

            // 3) Descontar stock de cada producto (seguro)
            for (i in req.items) {
                val affected = productDao.safeDecrementStock(i.productId, i.qty, now)
                if (affected == 0) {
                    // Revertirá toda la transacción
                    throw IllegalArgumentException("Stock insuficiente para producto ${i.productId}")
                }
            }

            saleId
        }
    }

    suspend fun getSale(id: Long): SaleWithDetails? = saleDao.getSaleWithDetails(id)
}
