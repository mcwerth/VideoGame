package com.example.vaultofluck.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.vaultofluck.data.local.entity.QuestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestDao {
    @Query("SELECT * FROM quest")
    fun observeQuests(): Flow<List<QuestEntity>>

    @Query("SELECT * FROM quest")
    suspend fun getAll(): List<QuestEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(quests: List<QuestEntity>)

    @Update
    suspend fun update(quest: QuestEntity)

    @Query("SELECT * FROM quest WHERE id = :id")
    suspend fun get(id: Int): QuestEntity?
}
