package com.example.smigoal.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MessageDAO {
    @Query("SELECT * FROM MESSAGE_TABLE")
    fun getMessage(): MessageEntity

    @Delete
    fun deleteMessage(messageEntity: MessageEntity)

    @Insert
    fun insertMessage(messageEntity: MessageEntity)
}