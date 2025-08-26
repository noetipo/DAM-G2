// com/example/ventas/data/local/entity/Sale.kt
package com.example.ventas.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Montos en centavos (S/ 12.50 => 1250)
 */
@Entity(tableName = "sales")
data class Sale(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clientId: Long?,                     // puede ser nulo para venta mostrador
    val issueAt: Long = System.currentTimeMillis(),  // fecha/hora emisión
    val series: String? = null,              // p.ej. F001
    val number: String? = null,              // p.ej. 000123
    val paymentMethod: String = "EFECTIVO",  // EFECTIVO|TARJETA|YAPE...
    val status: String = "EMITIDO",          // EMITIDO|ANULADO
    val subtotal: Long = 0,
    val tax: Long = 0,
    val total: Long = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
