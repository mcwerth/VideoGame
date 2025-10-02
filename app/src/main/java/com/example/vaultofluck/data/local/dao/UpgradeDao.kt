package com.example.vaultofluck.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.vaultofluck.data.local.entity.UpgradeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UpgradeDao {
    @Query("SELECT * FROM upgrade")
    fun observeUpgrades(): Flow<List<UpgradeEntity>>

    @Query("SELECT * FROM upgrade")
    suspend fun getAll(): List<UpgradeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(upgrades: List<UpgradeEntity>)

    @Update
    suspend fun update(upgrade: UpgradeEntity)

    @Query("SELECT * FROM upgrade WHERE id = :id")
    suspend fun get(id: Int): UpgradeEntity?
}
