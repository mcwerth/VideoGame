package com.example.vaultofluck.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.vaultofluck.data.local.entity.GachaHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GachaHistoryDao {
    @Query("SELECT * FROM gacha_history ORDER BY timestamp DESC LIMIT 50")
    fun recentHistory(): Flow<List<GachaHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: GachaHistoryEntity)
}
