package ru.mobiledevschool.todoapp

import android.app.Application
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
import java.util.concurrent.TimeUnit

class ToDoApp : Application() {
    override fun onCreate() {
        super.onCreate()

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

            single { ToDoRepositoryImpl(get(), get()) }
            single { LocalDB.createToDoItemsDao(this@ToDoApp) }
            single { AuthInterceptor() }

            single { OkHttpClient.Builder()
                .callTimeout(0, TimeUnit.MILLISECONDS)
                .connectTimeout(15000, TimeUnit.MILLISECONDS)
                .readTimeout(15000, TimeUnit.MILLISECONDS)
                .writeTimeout(15000, TimeUnit.MILLISECONDS)
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(AuthInterceptor())
                .build() }

            single { Retrofit.Builder()
                .baseUrl("https://beta.mrdekk.ru/todobackend/")
                .client(get())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ItemsApi::class.java)
            }
        }

        startKoin {
            androidContext(this@ToDoApp)
            modules(listOf(myModule))
        }
    }

//    companion object {
//
//        @Volatile
//        private var liveInstance: ToDoRepositoryImpl? = null
//
//        fun getLiveInstance() =
//            liveInstance ?: synchronized(this) {
//                liveInstance ?: ToDoRepositoryImpl().also { liveInstance = it }
//            }
//    }


}