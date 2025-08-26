// com/example/ventas/data/local/entity/SaleDetail.kt
package com.example.ventas.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sale_details")
data class SaleDetail(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val saleId: Long,
    val productId: Long,
    val qty: Int,                  // cantidad
    val unitPrice: Long,           // en centavos
    val lineTotal: Long,           // qty * unitPrice
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
