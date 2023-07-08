package ru.mobiledevschool.todoapp.local
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.mobiledevschool.todoapp.local.entity.ToDoItemEntity

/*
* Абстрактный класс-наследник RoomDB, содержит информацию о создаваемой БД
* */
@Database(entities = [ToDoItemEntity::class], version = 1, exportSchema = false)
abstract class ToDoItemsDatabase : RoomDatabase() {
    abstract fun toDoItemsDao(): ToDoItemsDao

    companion object {
        private var INSTANCE: ToDoItemsDatabase? = null
        private val LOCK = Any()

        fun getInstance(context: Context): ToDoItemsDatabase {
            INSTANCE?.let {
                return it
            }
            synchronized(LOCK) {
                INSTANCE?.let {
                    return it
                }
                val db = Room.databaseBuilder(
                    context,
                    ToDoItemsDatabase::class.java,
                    "toDoItems.db"
                )
                    .build()
                INSTANCE = db
                return db
            }
        }
    }
}