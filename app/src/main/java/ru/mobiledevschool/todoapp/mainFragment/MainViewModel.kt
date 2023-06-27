package ru.mobiledevschool.todoapp.mainFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.mobiledevschool.todoapp.repo.LiveDataRepository

class MainViewModel(private val repo: LiveDataRepository) : ViewModel() {

    val showDone = repo.showDone

    val listToShow = repo.toDoList

    val doneQuantity = repo.doneSize

//    fun getItems() = viewModelScope.launch{
//        repo.getItems()
//    }

    fun deleteItemById(id: Int) {
        repo.deleteItemById(id)
    }

    fun checkItemById(id: Int) {
        repo.checkItemById(id)
    }
    fun changeVisibility() {
        repo.changeVisibility()
    }

    class Factory(private val repo: LiveDataRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(repo) as T
            }
            throw IllegalArgumentException("Unable to construct viewModel")
        }
    }

}
