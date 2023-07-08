package ru.mobiledevschool.todoapp.di

import android.app.Application
import android.content.Context
import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.CoroutineScope
import ru.mobiledevschool.todoapp.ToDoApp
import javax.inject.Scope

@Scope
annotation class AppScope

@Component(modules = [DatasourceModule::class, ConnectivityModule::class])
@AppScope
interface AppComponent {
    fun activityComponent(): ActivityComponent
    fun applicationScope(): CoroutineScope
    fun inject(toDoApp: ToDoApp)
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}