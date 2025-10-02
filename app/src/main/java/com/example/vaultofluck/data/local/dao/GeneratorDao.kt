package com.example.vaultofluck.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.vaultofluck.data.local.entity.GeneratorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GeneratorDao {
    @Query("SELECT * FROM generator ORDER BY tier ASC")
    fun observeGenerators(): Flow<List<GeneratorEntity>>

    @Query("SELECT * FROM generator ORDER BY tier ASC")
    suspend fun getGenerators(): List<GeneratorEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(generators: List<GeneratorEntity>)

    @Update
    suspend fun update(generator: GeneratorEntity)

    @Query("SELECT * FROM generator WHERE id = :id")
    suspend fun get(id: Int): GeneratorEntity?
}
