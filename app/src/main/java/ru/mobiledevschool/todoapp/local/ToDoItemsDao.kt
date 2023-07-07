package ru.mobiledevschool.todoapp.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.mobiledevschool.todoapp.local.entity.ToDoItemEntity

/*
* Интерфейс, содержащий функции, по которым Room позволяет осуществлять SQLite запросы
* */
@Dao
interface ToDoItemsDao {

    @Query("SELECT * FROM toDoItems")
    fun getItems(): Flow<List<ToDoItemEntity>>

    @Query("SELECT COUNT(*) FROM toDoItems WHERE completed = 1")
    fun getDoneQuantity(): Flow<Int>

    @Query("SELECT * FROM toDoItems WHERE id = :id")
    suspend fun getItemById(id: String): ToDoItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg toDoItems: ToDoItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveItem(toDoItem: ToDoItemEntity)

    @Query("DELETE FROM toDoItems")
    suspend fun deleteAllItems()

    @Query("DELETE FROM toDoItems where id = :id")
    suspend fun deleteItemById(id: String)

}