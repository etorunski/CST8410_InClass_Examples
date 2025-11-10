package com.algonquincollege.torunse

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface ItemDAO {

    @Insert
    fun insertMessage(message:ShoppingItem) :Long

    @Delete
    fun deleteMessage(message:ShoppingItem):Int

    @Query("Select * from Items")
    fun getAllItems():List<ShoppingItem>
}