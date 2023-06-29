package ru.mobiledevschool.todoapp.repo

interface ToDoRepository {

    suspend fun refreshItems(){
    }
}
