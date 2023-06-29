package ru.mobiledevschool.todoapp.remote

import androidx.lifecycle.LiveData
import ru.mobiledevschool.todoapp.repo.ToDoItem

interface ToDoDataSource {

    fun observeTasks(): LiveData<ToDoItem>

    suspend fun getTasks(): List<ToDoItem>

    suspend fun refreshTasks()

//    fun observeTask(taskId: String): LiveData<ToDoItem>

    suspend fun getTask(taskId: String): ToDoItem

//    suspend fun refreshTask(taskId: String)

    suspend fun saveTask(toDoItem: ToDoItem)

    suspend fun completeTask(toDoItem: ToDoItem)

//    suspend fun completeTask(taskId: String)

//    suspend fun activateTask(task: Task)

//    suspend fun activateTask(taskId: String)

//    suspend fun clearCompletedTasks()

//    suspend fun deleteAllTasks()

    suspend fun deleteTask(taskId: String)
}