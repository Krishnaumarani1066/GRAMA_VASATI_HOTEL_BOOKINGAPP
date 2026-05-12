package com.yuriikonovalov.helia.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val content: String,
    val role: String, // "user" or "assistant"
    val timestamp: Long = System.currentTimeMillis()
)