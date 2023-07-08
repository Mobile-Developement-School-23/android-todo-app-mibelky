package ru.mobiledevschool.todoapp.di

import dagger.Subcomponent
import ru.mobiledevschool.todoapp.newItemFragment.ViewModelFactory
import ru.mobiledevschool.todoapp.newItemFragment.NewItemFragment
import ru.mobiledevschool.todoapp.newItemFragment.NewItemViewModel
import javax.inject.Scope

@Scope
annotation class NewItemFragmentScope

@Subcomponent
@NewItemFragmentScope
interface NewItemFragmentComponent {
    fun inject(fragment: NewItemFragment)
    fun newItemViewModel(): NewItemViewModel
    fun viewModelsFactory(): ViewModelFactory
}