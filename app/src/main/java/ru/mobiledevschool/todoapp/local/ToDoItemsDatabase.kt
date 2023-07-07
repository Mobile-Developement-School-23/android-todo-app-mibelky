package ru.mobiledevschool.todoapp.local
import androidx.room.Database
import androidx.room.RoomDatabase
import ru.mobiledevschool.todoapp.local.entity.ToDoItemEntity

/*
* Абстрактный класс-наследник RoomDB, содержит информацию о создаваемой БД
* */
@Database(entities = [ToDoItemEntity::class], version = 1, exportSchema = false)
abstract class ToDoItemsDatabase : RoomDatabase() {
    abstract fun toDoItemsDao(): ToDoItemsDao
}