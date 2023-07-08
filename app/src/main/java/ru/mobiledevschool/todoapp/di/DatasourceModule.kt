package ru.mobiledevschool.todoapp.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.mobiledevschool.todoapp.local.ToDoItemsDao
import ru.mobiledevschool.todoapp.local.ToDoItemsDatabase
import ru.mobiledevschool.todoapp.remote.AuthInterceptor
import ru.mobiledevschool.todoapp.remote.ItemsApi
import ru.mobiledevschool.todoapp.repo.ToDoRepository
import ru.mobiledevschool.todoapp.repo.ToDoRepositoryImpl
import java.util.concurrent.TimeUnit

@Module
interface DatasourceModule {

    companion object {
        private const val TIMEOUT_MILLIS = 15000L
        private const val BASE_URL = "https://beta.mrdekk.ru/todobackend/"


        @AppScope
        @Provides
        fun provideAuthInterceptor(): AuthInterceptor {
            return AuthInterceptor()
        }

        @AppScope
        @Provides
        fun okHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
            return OkHttpClient.Builder()
                .callTimeout(0, TimeUnit.MILLISECONDS)
                .connectTimeout( TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .readTimeout( TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .writeTimeout( TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(authInterceptor)
                .build()
        }

        @AppScope
        @Provides
        fun provideApi(okHttpClient: OkHttpClient) = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(ItemsApi::class.java)

        @AppScope
        @Provides
        fun provideDatabase(context: Context): ToDoItemsDatabase {
            return ToDoItemsDatabase.getInstance(context)
        }

        @AppScope
        @Provides
        fun provideDao(appDatabase: ToDoItemsDatabase): ToDoItemsDao {
            return appDatabase.toDoItemsDao()
        }
    }

    @Binds
    fun bindTodoItemRepository(impl: ToDoRepositoryImpl): ToDoRepository
}