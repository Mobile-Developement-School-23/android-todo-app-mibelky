package ru.mobiledevschool.todoapp

import android.app.Application
import android.os.Build
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.mobiledevschool.todoapp.connectivity.NetworkConnectivityObserver
import ru.mobiledevschool.todoapp.di.AppComponent
import ru.mobiledevschool.todoapp.di.DaggerAppComponent
import ru.mobiledevschool.todoapp.mainFragment.MainViewModel
import ru.mobiledevschool.todoapp.newItemFragment.NewItemViewModel
import ru.mobiledevschool.todoapp.remote.AuthInterceptor
import ru.mobiledevschool.todoapp.remote.ItemsApi
import ru.mobiledevschool.todoapp.repo.ToDoRepositoryImpl
import ru.mobiledevschool.todoapp.work.RefreshDataWorker
import java.util.concurrent.TimeUnit
/**
 * Application class нашего приложения, здесь располагается DI структур нашего приложения с наиболее
 * длительным жизненным циклом
 */
class ToDoApp : Application() {
    lateinit var appComponent: AppComponent
         private set
    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.factory().create(context = this)
        delayedInit()
    }

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    private fun delayedInit() {
        applicationScope.launch {
            setupRecurringWork()
        }
    }

    private fun setupRecurringWork() {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresBatteryNotLow(true)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setRequiresDeviceIdle(true)
                }
            }.build()

        val repeatingRequest =
            PeriodicWorkRequestBuilder<RefreshDataWorker>(8, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()

        WorkManager.getInstance().enqueueUniquePeriodicWork(
            RefreshDataWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest
        )
    }

}
