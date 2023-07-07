package ru.mobiledevschool.todoapp.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import retrofit2.HttpException
import ru.mobiledevschool.todoapp.local.ToDoItemsDao
import ru.mobiledevschool.todoapp.repo.ToDoRepositoryImpl
/*
* Класс содержит метод doWork() с для периодического обновления данных с сервером.
 */
class RefreshDataWorker(appContext: Context, params: WorkerParameters, private val repository: ToDoRepositoryImpl):
    CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            repository.refreshItems()
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}