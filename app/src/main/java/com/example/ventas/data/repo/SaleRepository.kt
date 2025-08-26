// com/example/ventas/data/repo/SaleRepository.kt
package com.example.ventas.data.repo

import androidx.room.withTransaction
import com.example.ventas.data.local.AppDatabase
import com.example.ventas.data.local.dao.ProductDao
import com.example.ventas.data.local.dao.SaleDao
import com.example.ventas.data.local.entity.Sale
import com.example.ventas.data.local.entity.SaleDetail
import com.example.ventas.data.local.entity.SaleWithClientAndDetails
import kotlinx.coroutines.flow.Flow

class SaleRepository(
    private val db: AppDatabase,
    private val saleDao: SaleDao,
    private val productDao: ProductDao
) {

    /** Observa ventas con cliente y detalles (para listas en UI). */
    fun observeSales(): Flow<List<SaleWithClientAndDetails>> =
        saleDao.observeSalesWithClient()

    /**
     * Crea la venta calculando subtotal/IGV/total, genera F001-######,
     * inserta detalles y descuenta stock de forma atómica.
     * Lanza IllegalArgumentException si stock insuficiente o datos inválidos.
     */
    suspend fun createSale(req: CreateSaleRequest): Long {
        require(req.items.isNotEmpty()) { "Debe incluir al menos un ítem" }
        require(req.items.all { it.qty > 0 && it.unitPrice >= 0 }) { "Cantidades y precios inválidos" }

        val subtotal = req.items.fold(0L) { acc, it -> acc + it.unitPrice * it.qty }
        val tax = kotlin.math.round(subtotal * req.taxRate).toLong()
        val total = subtotal + tax
        val now = System.currentTimeMillis()

        return db.withTransaction {
            // Serie fija:
            val series = "F001"

            // Último correlativo de la serie:
            val lastNumberStr = saleDao.getLastNumber(series)
            val nextNumberInt = (lastNumberStr?.toIntOrNull() ?: 0) + 1
            val nextNumberStr = nextNumberInt.toString().padStart(6, '0') // 000001

            // 1) Insertar cabecera
            val saleId = saleDao.insertSale(
                Sale(
                    clientId = req.clientId,
                    paymentMethod = req.paymentMethod,
                    series = series,
                    number = nextNumberStr,
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

            // 3) Descontar stock (seguro)
            for (i in req.items) {
                val affected = productDao.safeDecrementStock(i.productId, i.qty, now)
                if (affected == 0) {
                    // Revierte toda la transacción
                    throw IllegalArgumentException("Stock insuficiente para producto ${i.productId}")
                }
            }

            saleId
        }
    }

    /** Trae una venta con cliente y detalles. */
    suspend fun getSale(id: Long): SaleWithClientAndDetails? =
        saleDao.getSaleWithClientAndDetails(id)
}
