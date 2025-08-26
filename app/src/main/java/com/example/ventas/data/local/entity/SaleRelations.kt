// com/example/ventas/data/local/entity/SaleRelations.kt
package com.example.ventas.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class SaleWithDetails(
    @Embedded val sale: Sale,
    @Relation(
        parentColumn = "id",
        entityColumn = "saleId"
    )
    val details: List<SaleDetail>
)
