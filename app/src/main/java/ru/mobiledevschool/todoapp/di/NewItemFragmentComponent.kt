package ru.mobiledevschool.todoapp.di

import dagger.Subcomponent
import ru.mobiledevschool.todoapp.mainFragment.MainFragment
import ru.mobiledevschool.todoapp.newItemFragment.NewItemFragment
import javax.inject.Scope

@Scope
annotation class NewItemFragmentScope

@Subcomponent
@NewItemFragmentScope
interface NewItemFragmentComponent {
    fun inject(fragment: NewItemFragment)
}