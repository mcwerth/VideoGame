package com.example.vaultofluck.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.vaultofluck.data.local.entity.RunEntity
import com.example.vaultofluck.domain.model.RunStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface RunDao {
    @Query("SELECT * FROM run WHERE status = :status ORDER BY startedAt DESC LIMIT 1")
    suspend fun latestWithStatus(status: RunStatus): RunEntity?

    @Query("SELECT * FROM run ORDER BY startedAt DESC LIMIT 10")
    fun recentRuns(): Flow<List<RunEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(run: RunEntity): Long

    @Update
    suspend fun update(run: RunEntity)
}
