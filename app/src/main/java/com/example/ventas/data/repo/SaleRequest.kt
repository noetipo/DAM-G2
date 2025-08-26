// com/example/ventas/data/repo/SaleRequest.kt
package com.example.ventas.data.repo

data class SaleItemRequest(
    val productId: Long,
    val qty: Int,
    val unitPrice: Long   // en centavos; se puede traer del producto en UI
)

data class CreateSaleRequest(
    val clientId: Long?,
    val paymentMethod: String = "EFECTIVO",
    val series: String? = null,
    val number: String? = null,
    val items: List<SaleItemRequest>,
    val taxRate: Double = 0.18          // IGV 18%, ajusta si corresponde
)
