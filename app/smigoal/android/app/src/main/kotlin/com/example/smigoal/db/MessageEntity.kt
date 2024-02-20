package com.example.smigoal.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity(tableName = "MESSAGE_TABLE")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val url: String?,
    val message: String,
    val sender: String,
    val containsUrl: Boolean,
    val timestamp: Long,
    val isSmishing: Boolean
) {
    constructor(url: String?, message: String, sender: String, containsUrl: Boolean, timestamp: Long, isSmishing: Boolean)
            : this(null, url, message, sender, containsUrl, timestamp, isSmishing)
}