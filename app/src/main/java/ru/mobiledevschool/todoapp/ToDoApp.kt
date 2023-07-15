package ru.mobiledevschool.todoapp

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContentProviderCompat.requireContext
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
import ru.mobiledevschool.todoapp.di.AppScope
import ru.mobiledevschool.todoapp.di.DaggerAppComponent
import ru.mobiledevschool.todoapp.local.ToDoItemsDao
import ru.mobiledevschool.todoapp.local.entity.asDomainModel
import ru.mobiledevschool.todoapp.mainFragment.MainViewModel
import ru.mobiledevschool.todoapp.newItemFragment.NewItemViewModel
import ru.mobiledevschool.todoapp.notification.AlarmReceiver
import ru.mobiledevschool.todoapp.remote.AuthInterceptor
import ru.mobiledevschool.todoapp.remote.ItemsApi
import ru.mobiledevschool.todoapp.repo.ToDoItem
import ru.mobiledevschool.todoapp.repo.ToDoRepositoryImpl
import ru.mobiledevschool.todoapp.work.RefreshDataWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
 * Application class нашего приложения, здесь располагается DI структур нашего приложения с наиболее
 * длительным жизненным циклом
 */
class ToDoApp : Application() {
    lateinit var appComponent: AppComponent
        private set

    @Inject
    lateinit var dao: ToDoItemsDao

    private val applicationScope by lazy { appComponent.applicationScope() }
    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.factory().create(context = this)
        appComponent.inject(this)
        delayedInit()
        notificationInit()
        setAlarm()
    }

    private fun delayedInit() {
        applicationScope.launch {
            setupRecurringWork()
        }
    }

    private fun notificationInit() {
        val notificationChannel =
            NotificationChannel(
                "channel_id",
                "to-do notifications",
                NotificationManager.IMPORTANCE_HIGH
            )

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun setAlarm() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // adding intent and pending intent to go to AlarmReceiver Class in future

        applicationScope.launch {
            val toDoS: List<ToDoItem> = dao.getItemsList().asDomainModel().filter {
                !it.completed && it.deadLine != null
            }
            for (toDo in toDoS) {
                val intent = Intent(applicationContext, AlarmReceiver::class.java)
                intent.putExtra("toDo", toDo)
                val pendingIntent = PendingIntent.getBroadcast(
                    applicationContext,
                    toDo.hashCode(),
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )
                val mainActivityIntent = Intent(applicationContext, MainActivity::class.java)
                val basicPendingIntent = PendingIntent.getActivity(
                    applicationContext,
                    toDo.hashCode(),
                    mainActivityIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )
                val clockInfo =
                    AlarmManager.AlarmClockInfo(toDo.deadLine!!.time, basicPendingIntent)
                alarmManager.setAlarmClock(clockInfo, pendingIntent)
            }
        }
    }

    private fun setupRecurringWork() {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresBatteryNotLow(true)
            .apply {
                setRequiresDeviceIdle(true)
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
