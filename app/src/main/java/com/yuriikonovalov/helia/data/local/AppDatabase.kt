package com.yuriikonovalov.helia.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yuriikonovalov.helia.data.local.dao.MessageDao
import com.yuriikonovalov.helia.data.local.model.MessageEntity

@Database(
    entities = [MessageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
}