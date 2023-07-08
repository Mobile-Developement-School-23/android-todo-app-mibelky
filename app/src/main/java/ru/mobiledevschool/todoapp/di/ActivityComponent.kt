package ru.mobiledevschool.todoapp.di

import dagger.Subcomponent
import javax.inject.Scope

@Scope
annotation class ActivityScope

@Subcomponent
@ActivityScope
interface ActivityComponent {
    fun mainFragmentComponent(): MainFragmentComponent
    fun newItemFragmentComponent(): NewItemFragmentComponent
}