package ru.mobiledevschool.todoapp

import android.app.Application
import ru.mobiledevschool.todoapp.repo.LiveDataRepository
import ru.mobiledevschool.todoapp.repo.ToDoItemsRepository

class ToDoApp: Application() {

    companion object Repo {

        @Volatile
        private var liveInstance: LiveDataRepository? = null

        fun getLiveInstance() =
            liveInstance ?: synchronized(this) {
                liveInstance ?: LiveDataRepository().also { liveInstance = it }
            }
    }

}