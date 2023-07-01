package ru.mobiledevschool.todoapp.repo

interface ToDoRepository {

    suspend fun refreshItems()
    suspend fun getItemById(id: String): ToDoItem?
    suspend fun addItem(toDoItem: ToDoItem)
    suspend fun deleteItem(toDoItem: ToDoItem)
    suspend fun changeItem(toDoItem: ToDoItem)
}
