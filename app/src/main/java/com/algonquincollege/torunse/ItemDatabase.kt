package com.algonquincollege.torunse

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ShoppingItem::class], version = 1)
abstract class ItemDatabase : RoomDatabase() {
    abstract fun getMyDAO(): ItemDAO
}