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
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.mobiledevschool.todoapp.local.LocalDB
import ru.mobiledevschool.todoapp.mainFragment.MainViewModel
import ru.mobiledevschool.todoapp.newItemFragment.NewItemViewModel
import ru.mobiledevschool.todoapp.remote.AuthInterceptor
import ru.mobiledevschool.todoapp.remote.ItemsApi
import ru.mobiledevschool.todoapp.repo.ToDoRepositoryImpl
import ru.mobiledevschool.todoapp.work.RefreshDataWorker
import java.util.concurrent.TimeUnit

class ToDoApp : Application() {

    override fun onCreate() {
        super.onCreate()
        delayedInit()

        /**
         * use Koin Library as a service locator
         */
        val myModule = module {

            viewModel {
                MainViewModel(
                    get() as ToDoRepositoryImpl
                )
            }

            viewModel {
                NewItemViewModel(
                    get() as ToDoRepositoryImpl
                )
            }

            single { ToDoRepositoryImpl(get(), get(), get(), appScope = get()) }
            single { LocalDB.createToDoItemsDao(this@ToDoApp) }
            single { AuthInterceptor() }
            single { CoroutineScope(Dispatchers.Default) }

            single {
                OkHttpClient.Builder()
                    .callTimeout(0, TimeUnit.MILLISECONDS)
                    .connectTimeout(15000, TimeUnit.MILLISECONDS)
                    .readTimeout(15000, TimeUnit.MILLISECONDS)
                    .writeTimeout(15000, TimeUnit.MILLISECONDS)
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .addInterceptor(AuthInterceptor())
                    .build()
            }

            single {
                Retrofit.Builder()
                    .baseUrl("https://beta.mrdekk.ru/todobackend/")
                    .client(get())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(ItemsApi::class.java)
            }

            single {
                NetworkConnectivityObserver(get())
            }
        }

        startKoin {
            androidContext(this@ToDoApp)
            modules(listOf(myModule))
        }
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
