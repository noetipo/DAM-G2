// com/example/ventas/data/local/dao/SaleDao.kt
package com.example.ventas.data.local.dao

import androidx.room.*
import com.example.ventas.data.local.entity.Sale
import com.example.ventas.data.local.entity.SaleDetail
import com.example.ventas.data.local.entity.SaleWithClientAndDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {

    /* -------------------- Mutaciones -------------------- */

    @Insert
    suspend fun insertSale(sale: Sale): Long

    @Insert
    suspend fun insertDetails(details: List<SaleDetail>)

    @Update
    suspend fun updateSale(sale: Sale)

    @Query("UPDATE sales SET status = :newStatus, updatedAt = :ts WHERE id = :saleId")
    suspend fun updateStatus(saleId: Long, newStatus: String, ts: Long = System.currentTimeMillis()): Int

    /* -------------------- Lecturas (con cliente + detalles) -------------------- */

    @Transaction
    @Query("SELECT * FROM sales WHERE id = :id")
    suspend fun getSaleWithClientAndDetails(id: Long): SaleWithClientAndDetails?

    @Transaction
    @Query("""
        SELECT * FROM sales 
        WHERE status != 'ANULADO'
        ORDER BY issueAt DESC, id DESC
    """)
    fun observeSalesWithClient(): Flow<List<SaleWithClientAndDetails>>

    /* -------------------- Filtros útiles -------------------- */

    // Ventas por cliente (excluye anuladas)
    @Transaction
    @Query("""
        SELECT * FROM sales
        WHERE status != 'ANULADO'
          AND (:clientId IS NULL OR clientId = :clientId)
        ORDER BY issueAt DESC, id DESC
    """)
    fun observeByClient(clientId: Long?): Flow<List<SaleWithClientAndDetails>>

    // Ventas por rango de fechas (timestamps en millis)
    @Transaction
    @Query("""
        SELECT * FROM sales
        WHERE status != 'ANULADO'
          AND issueAt BETWEEN :fromTs AND :toTs
        ORDER BY issueAt DESC, id DESC
    """)
    fun observeByDateRange(fromTs: Long, toTs: Long): Flow<List<SaleWithClientAndDetails>>

    /* -------------------- Correlativo -------------------- */

    // Último número por serie. Al ser 6 dígitos con ceros a la izquierda, el orden por id está ok;
    // si prefieres numérico puro, usa CAST(number AS INTEGER) y asegurarte de que siempre sea numérico.
    @Query("SELECT number FROM sales WHERE series = :series ORDER BY id DESC LIMIT 1")
    suspend fun getLastNumber(series: String): String?
}
