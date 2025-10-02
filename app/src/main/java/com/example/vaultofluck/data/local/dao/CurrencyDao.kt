package com.example.vaultofluck.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.vaultofluck.data.local.entity.CurrencyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {
    @Query("SELECT * FROM currency")
    fun observeCurrencies(): Flow<List<CurrencyEntity>>

    @Query("SELECT * FROM currency")
    suspend fun getAll(): List<CurrencyEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(currencies: List<CurrencyEntity>)

    @Update
    suspend fun update(currency: CurrencyEntity)

    @Query("UPDATE currency SET amount = amount + :delta WHERE name = :name")
    suspend fun increment(name: String, delta: Double)

    @Query("SELECT * FROM currency WHERE name = :name")
    suspend fun getCurrency(name: String): CurrencyEntity?
}
