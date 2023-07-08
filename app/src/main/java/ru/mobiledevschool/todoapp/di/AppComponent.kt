package ru.mobiledevschool.todoapp.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import javax.inject.Scope

@Scope
annotation class AppScope

@Component(modules = [DatasourceModule::class])
@AppScope
interface AppComponent {
    fun activityComponent(): ActivityComponent

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}