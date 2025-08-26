package com.example.ventas.data.local.dao

import androidx.room.*
import com.example.ventas.data.local.entity.Client
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {
    @Query("SELECT * FROM clients WHERE isActive = 1 ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<Client>>

    @Query("""
        SELECT * FROM clients 
        WHERE isActive = 1 
        AND (fullName LIKE '%' || :q || '%' OR document LIKE '%' || :q || '%')
        ORDER BY updatedAt DESC
    """)
    fun search(q: String): Flow<List<Client>>

    @Query("SELECT * FROM clients WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): Client?

    @Insert
    suspend fun insert(c: Client): Long

    @Update
    suspend fun update(c: Client)

    @Query("UPDATE clients SET isActive = 0, updatedAt = :ts WHERE id = :id")
    suspend fun softDelete(id: Long, ts: Long = System.currentTimeMillis())
}
