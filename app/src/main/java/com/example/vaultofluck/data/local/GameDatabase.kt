package com.example.vaultofluck.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.vaultofluck.data.local.dao.CurrencyDao
import com.example.vaultofluck.data.local.dao.GachaHistoryDao
import com.example.vaultofluck.data.local.dao.GeneratorDao
import com.example.vaultofluck.data.local.dao.PlayerDao
import com.example.vaultofluck.data.local.dao.QuestDao
import com.example.vaultofluck.data.local.dao.RunDao
import com.example.vaultofluck.data.local.dao.UpgradeDao
import com.example.vaultofluck.data.local.entity.CurrencyEntity
import com.example.vaultofluck.data.local.entity.GachaHistoryEntity
import com.example.vaultofluck.data.local.entity.GeneratorEntity
import com.example.vaultofluck.data.local.entity.PlayerEntity
import com.example.vaultofluck.data.local.entity.QuestEntity
import com.example.vaultofluck.data.local.entity.RunEntity
import com.example.vaultofluck.data.local.entity.UpgradeEntity

@Database(
    entities = [
        PlayerEntity::class,
        CurrencyEntity::class,
        GeneratorEntity::class,
        UpgradeEntity::class,
        GachaHistoryEntity::class,
        RunEntity::class,
        QuestEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(EnumConverters::class)
abstract class GameDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun currencyDao(): CurrencyDao
    abstract fun generatorDao(): GeneratorDao
    abstract fun upgradeDao(): UpgradeDao
    abstract fun gachaHistoryDao(): GachaHistoryDao
    abstract fun runDao(): RunDao
    abstract fun questDao(): QuestDao

    companion object {
        fun build(context: Context): GameDatabase = Room.databaseBuilder(
            context,
            GameDatabase::class.java,
            "vault_of_luck.db"
        ).fallbackToDestructiveMigration().build()
    }
}
