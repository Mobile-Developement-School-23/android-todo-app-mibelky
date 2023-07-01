package ru.mobiledevschool.todoapp.local

import android.content.Context
import androidx.room.Room


object LocalDB {

    fun createToDoItemsDao(context: Context): ToDoItemsDao {
        return Room.databaseBuilder(
            context.applicationContext,
            ToDoItemsDatabase::class.java, "toDoItems.db"
        ).build().toDoItemsDao()
    }

}