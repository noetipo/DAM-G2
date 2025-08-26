// com/example/ventas/data/local/entity/SaleWithClientAndDetails.kt
package com.example.ventas.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class SaleWithClientAndDetails(
    @Embedded val sale: Sale,

    @Relation(
        parentColumn = "clientId",
        entityColumn = "id"
    )
    val client: Client?,   // puede ser null (venta mostrador)

    @Relation(
        parentColumn = "id",
        entityColumn = "saleId"
    )
    val details: List<SaleDetail>
)
