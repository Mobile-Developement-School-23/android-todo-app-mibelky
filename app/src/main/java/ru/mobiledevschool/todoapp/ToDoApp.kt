package ru.mobiledevschool.todoapp

import android.app.Application
import ru.mobiledevschool.todoapp.repo.ToDoRepositoryImpl

class ToDoApp : Application() {

    companion object {

        @Volatile
        private var liveInstance: ToDoRepositoryImpl? = null

        fun getLiveInstance() =
            liveInstance ?: synchronized(this) {
                liveInstance ?: ToDoRepositoryImpl().also { liveInstance = it }
            }
    }


}