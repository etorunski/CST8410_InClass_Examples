package com.algonquincollege.torunse

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface ItemDAO {

    @Insert
    suspend fun insertMessage(message:ShoppingItem) :Long

    @Delete
    suspend fun deleteMessage(message:ShoppingItem):Int

    @Update
    suspend fun updateMessage(message:ShoppingItem):Int


    @Query("Select * from Items")
    fun getAllItems(): List<ShoppingItem>
}