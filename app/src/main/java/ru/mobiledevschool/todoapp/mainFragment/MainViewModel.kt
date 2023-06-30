package ru.mobiledevschool.todoapp.mainFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.mobiledevschool.todoapp.repo.ToDoRepositoryImpl

class MainViewModel(private val repo: ToDoRepositoryImpl) : ViewModel() {

    val showDone = repo.showDone

    val listToShow = repo.remoteList
    //repo.toDoList

    val doneQuantity = repo.doneSize

    init {
        refreshItems()
    }

    private fun refreshItems() = viewModelScope.launch {
        repo.refreshItems()
    }


    fun deleteItemById(id: String) = viewModelScope.launch {
        repo.deleteItemById(id)
    }


    fun checkItemById(id: String) {
        repo.checkItemById(id)
    }

    fun changeVisibility() {
        repo.changeVisibility()
    }

    class Factory(private val repo: ToDoRepositoryImpl) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(repo) as T
            }
            throw IllegalArgumentException("Unable to construct viewModel")
        }
    }

}
