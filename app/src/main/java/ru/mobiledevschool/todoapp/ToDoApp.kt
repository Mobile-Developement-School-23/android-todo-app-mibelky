package ru.mobiledevschool.todoapp

import android.app.Application
import ru.mobiledevschool.todoapp.repo.ToDoItemsRepository

class ToDoApp: Application() {

    companion object Repo {
        @Volatile
        private var instance: ToDoItemsRepository? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: ToDoItemsRepository().also { instance = it }
            }
    }
}