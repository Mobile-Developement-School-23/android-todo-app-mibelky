package ru.mobiledevschool.todoapp.di

import dagger.Subcomponent
import ru.mobiledevschool.todoapp.mainFragment.MainFragment
import ru.mobiledevschool.todoapp.mainFragment.MainViewModel
import ru.mobiledevschool.todoapp.mainFragment.ViewModelFactory
import javax.inject.Scope

@Scope
annotation class MainFragmentScope

@Subcomponent
@MainFragmentScope
interface MainFragmentComponent {
    fun inject(fragment: MainFragment)
    fun mainViewModel(): MainViewModel
    fun viewModelsFactory(): ViewModelFactory
}