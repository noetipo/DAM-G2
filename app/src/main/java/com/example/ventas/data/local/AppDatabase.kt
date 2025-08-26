// com/example/ventas/data/local/AppDatabase.kt
package com.example.ventas.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.ventas.data.local.dao.*
import com.example.ventas.data.local.entity.*

@Database(
    entities = [
        Product::class,
        Client::class,
        Sale::class,
        SaleDetail::class
    ],
    version = 3,                   // ⬅️ súbelo desde la v2 a v3
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun clientDao(): ClientDao
    abstract fun saleDao(): SaleDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "ventas.db")
                    .fallbackToDestructiveMigration() // en dev
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
