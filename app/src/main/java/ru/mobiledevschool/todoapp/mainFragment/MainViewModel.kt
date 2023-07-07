package ru.mobiledevschool.todoapp.mainFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.mobiledevschool.todoapp.repo.ToDoItem
import ru.mobiledevschool.todoapp.repo.ToDoRepositoryImpl

class MainViewModel(private val repo: ToDoRepositoryImpl) : ViewModel() {

    val showDone = MutableLiveData<Boolean>(true)

    private val _listToShow = MutableLiveData<List<ToDoItem>>()
    val listToShow : LiveData<List<ToDoItem>>
        get() = _listToShow

    val exceptionMessageEvent = repo.exceptionMessageEvent
    fun endHttpExceptionCodeEvent() = repo.endExceptionMessageEvent()

    private val _navigateEvent = MutableLiveData<String?>("null")
    val navigateEvent :LiveData<String?> = _navigateEvent

    private val _doneQuantity = MutableLiveData<Int>()
    val doneQuantity: LiveData<Int>  = _doneQuantity
    init {
        viewModelScope.launch {
            repo.getAllItems().collect { items ->
                _listToShow.postValue(items)
            }
        }
        collectDoneQuantity()
        refreshItems()
    }
    private fun refreshItems() = viewModelScope.launch {
        repo.refreshItems()
    }

    private fun collectDoneQuantity() = viewModelScope.launch {
        repo.getDoneQuantity().collect{ _doneQuantity.postValue(it) }
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

    fun changeVisibility() =  repo.changeVisibility()
    fun startNavigateEvent(id: String?) {
        _navigateEvent.value = id
    }

    fun endNavigateEvent() {
        _navigateEvent.value = "null"
    }
}
