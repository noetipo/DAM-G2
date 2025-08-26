// com/example/ventas/data/local/dao/SaleDao.kt
package com.example.ventas.data.local.dao

import androidx.room.*
import com.example.ventas.data.local.entity.Sale
import com.example.ventas.data.local.entity.SaleDetail
import com.example.ventas.data.local.entity.SaleWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {

    @Insert
    suspend fun insertSale(sale: Sale): Long

    @Insert
    suspend fun insertDetails(details: List<SaleDetail>)

    @Update
    suspend fun updateSale(sale: Sale)

    @Transaction
    @Query("SELECT * FROM sales WHERE id = :id")
    suspend fun getSaleWithDetails(id: Long): SaleWithDetails?

    @Transaction
    @Query("""
        SELECT * FROM sales 
        WHERE status != 'ANULADO'
        ORDER BY issueAt DESC
    """)
    fun observeSales(): Flow<List<SaleWithDetails>>
}
