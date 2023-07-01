package ru.mobiledevschool.todoapp.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.mobiledevschool.todoapp.dto.ToDoItemDTO


@Dao
interface ToDoItemsDao {

    @Query("SELECT * FROM toDoItems")
    fun getItems(): Flow<List<ToDoItemDTO>>

    @Query("SELECT * FROM toDoItems WHERE completed = 0")
    suspend fun getUndoneItems(): List<ToDoItemDTO>

    @Query("SELECT * FROM toDoItems WHERE id = :id")
    suspend fun getItemById(id: String): ToDoItemDTO?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg toDoItems: ToDoItemDTO)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveItem(toDoItem: ToDoItemDTO)

    @Query("DELETE FROM toDoItems")
    suspend fun deleteAllItems()

    @Query("DELETE FROM toDoItems where id = :id")
    suspend fun deleteItemById(id: String)

}