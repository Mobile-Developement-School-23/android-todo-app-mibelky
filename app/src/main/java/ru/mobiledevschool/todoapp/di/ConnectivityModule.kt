package ru.mobiledevschool.todoapp.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.mobiledevschool.todoapp.connectivity.ConnectivityObserver
import ru.mobiledevschool.todoapp.connectivity.NetworkConnectivityObserver
import ru.mobiledevschool.todoapp.local.ToDoItemsDao
import ru.mobiledevschool.todoapp.local.ToDoItemsDatabase
import ru.mobiledevschool.todoapp.remote.AuthInterceptor
import ru.mobiledevschool.todoapp.remote.ItemsApi
import ru.mobiledevschool.todoapp.repo.ToDoRepository
import ru.mobiledevschool.todoapp.repo.ToDoRepositoryImpl
import java.util.concurrent.TimeUnit

@Module
interface ConnectivityModule {

    companion object {

        @AppScope
        @Provides
        fun provideAppScope(): CoroutineScope {
            return CoroutineScope(Dispatchers.Default)
        }

        @AppScope
        @Provides
        fun provideDispatcherIO(): CoroutineDispatcher {
            return Dispatchers.IO
        }

    }

    @AppScope
    @Binds
    fun provideConnectivityObserver(impl: NetworkConnectivityObserver): ConnectivityObserver
}