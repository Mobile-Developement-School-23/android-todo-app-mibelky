package ru.mobiledevschool.todoapp.local
import androidx.room.Database
import androidx.room.RoomDatabase
import ru.mobiledevschool.todoapp.dto.ToDoItemDTO

@Database(entities = [ToDoItemDTO::class], version = 1, exportSchema = false)
abstract class ToDoItemsDatabase : RoomDatabase() {

    abstract fun toDoItemsDao(): ToDoItemsDao
}