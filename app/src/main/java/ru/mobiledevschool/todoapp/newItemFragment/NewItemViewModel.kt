package ru.mobiledevschool.todoapp.newItemFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.mobiledevschool.todoapp.mainFragment.MainViewModel
import ru.mobiledevschool.todoapp.repo.ToDoItem
import ru.mobiledevschool.todoapp.repo.ToDoRepositoryImpl
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import javax.inject.Provider

/*
* ViewModel для NewItemFragment. Содержит состояния для View данного фрагмента, ссылается
*  на Repository, откуда берет и куда отправляет данные
* */
class NewItemViewModel @Inject constructor(val repo: ToDoRepositoryImpl) : ViewModel() {

    private val _item = MutableLiveData<ToDoItem?>(null)
    val item: LiveData<ToDoItem?> = _item

    private val _priority = MutableLiveData(ToDoItem.Priority.NORMAL)
    val priority: LiveData<ToDoItem.Priority> = _priority

    private val _highlightDeleteButton = MutableLiveData(false)
    val enableDelete: LiveData<Boolean> = _highlightDeleteButton

    private val _date = MutableLiveData<Long?>()
    val date: LiveData<Long?> = _date

    private val _saveEvent = MutableLiveData<Boolean>(false)
    val saveEvent: LiveData<Boolean> = _saveEvent

    private val _navigationEvent = MutableLiveData<Boolean>(false)
    val navigationEvent: LiveData<Boolean> = _navigationEvent

    fun getItemById(id: String) = viewModelScope.launch {
        _item.value = repo.getItemById(id)
        _item.value?.let {
            _priority.value = it.priority
            _date.value = it.deadLine?.time
        }

    }

    fun deleteItemById() = viewModelScope.launch {
        item.value?.let {
            repo.deleteItem(it)
        }
    }.invokeOnCompletion { startNavigationEvent() }

    fun addNewItem(text: String) {
        val calendar = Calendar.getInstance()
        val newItem =
            ToDoItem(
                text = text,
                completed = false,
                priority = priority.value ?: ToDoItem.Priority.NORMAL,
                deadLine = date.value?.let { Date(it) },
                creationDate = Date(calendar.timeInMillis),
                editionDate = Date(calendar.timeInMillis)
            )
        viewModelScope.launch { repo.addItem(newItem) }.invokeOnCompletion { startNavigationEvent() }
    }

    fun updateById(text: String) {
        val calendar = Calendar.getInstance()
        val changedItem = ToDoItem(
            id = item.value!!.id,
            text = text,
            completed = item.value!!.completed,
            priority = priority.value!!,
            deadLine = date.value?.let { Date(it) },
            creationDate = item.value!!.creationDate,
            editionDate = Date(calendar.timeInMillis)
        )
        viewModelScope.launch { repo.changeItem(changedItem) }.invokeOnCompletion { startNavigationEvent() }
    }

    fun updatePriority(priority: ToDoItem.Priority) {
        _priority.value = priority
    }

    fun updateDeadLine(date: Long) {
        _date.value = date
    }

    fun startHighlight() {
        _highlightDeleteButton.value = true
    }

    fun startSaveEvent() {
        _saveEvent.value = true
    }

    fun endSaveEvent() {
        _saveEvent.value = false
    }

    fun startNavigationEvent() {
        _navigationEvent.value = true
    }

    fun endNavigationEvent() {
        _navigationEvent.value = false
    }

    class Factory(private val repo: ToDoRepositoryImpl) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NewItemViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return NewItemViewModel(repo) as T
            }
            throw IllegalArgumentException("Unable to construct viewModel")
        }
    }
}

class ViewModelFactory @Inject constructor(
    newItemViewModelProvider: Provider<NewItemViewModel>
) : ViewModelProvider.Factory {
    private val providers = mapOf<Class<*>, Provider<out ViewModel>>(
        NewItemViewModel::class.java to newItemViewModelProvider
    )

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return providers[modelClass]!!.get() as T
    }
}