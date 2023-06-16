package ru.mobiledevschool.todoapp.mainFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import ru.mobiledevschool.todoapp.repo.LiveDataRepository
import ru.mobiledevschool.todoapp.repo.ToDoItem
import ru.mobiledevschool.todoapp.repo.ToDoItemsRepository
import java.text.FieldPosition

class MainViewModel(private val repo: LiveDataRepository) : ViewModel() {

    val showDone = repo.showDone

    val listToShow = repo.toDoList

    val doneQuantity = repo.doneSize
    fun deleteItemByPosition(position: Int) {
        repo.deleteItemByPosition(position)
    }

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
