package ru.mobiledevschool.todoapp.mainFragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.mobiledevschool.todoapp.repo.ToDoItem
import ru.mobiledevschool.todoapp.repo.ToDoRepositoryImpl

class MainViewModel(private val repo: ToDoRepositoryImpl) : ViewModel() {

    val showDone = MutableLiveData<Boolean>()//repo.showDone

    val listToShow = MutableLiveData<List<ToDoItem>>()

    val httpExceptionCodeEvent = repo.httpExceptionCodeEvent
    fun endHttpExceptionCodeEvent() = repo.endHttpExceptionCodeEvent()

    val doneQuantity = MutableLiveData<Int>()

    init {
        viewModelScope.launch {
            repo.getAllItems().collect { items ->
                listToShow.postValue(items)
                doneQuantity.postValue(items.count { it.completed })
            }
        }
        refreshItems()
    }
    private fun refreshItems() = viewModelScope.launch {
        repo.refreshItems()
    }
    fun deleteItem(toDoItem: ToDoItem) = viewModelScope.launch {
        repo.deleteItem(toDoItem)
    }
    fun checkItem(toDoItem: ToDoItem) = viewModelScope.launch {
        val checkedItem = ToDoItem(
            id = toDoItem.id,
            text = toDoItem.text,
            completed = !toDoItem.completed,
            priority = toDoItem.priority,
            deadLine = toDoItem.deadLine,
            creationDate = toDoItem.creationDate,
            editionDate = toDoItem.editionDate
        )
            repo.changeItem(checkedItem)
    }

    fun changeVisibility() {
        repo.changeVisibility()
    }
}
