package com.example.vaultofluck.data.local

import androidx.room.TypeConverter
import com.example.vaultofluck.domain.model.QuestType
import com.example.vaultofluck.domain.model.RunStatus
import com.example.vaultofluck.domain.model.UpgradeType

class EnumConverters {
    @TypeConverter
    fun fromUpgradeType(type: UpgradeType): String = type.name

    @TypeConverter
    fun toUpgradeType(value: String): UpgradeType = UpgradeType.valueOf(value)

    @TypeConverter
    fun fromQuestType(type: QuestType): String = type.name

    @TypeConverter
    fun toQuestType(value: String): QuestType = QuestType.valueOf(value)

    @TypeConverter
    fun fromRunStatus(status: RunStatus): String = status.name

    @TypeConverter
    fun toRunStatus(value: String): RunStatus = RunStatus.valueOf(value)
}
