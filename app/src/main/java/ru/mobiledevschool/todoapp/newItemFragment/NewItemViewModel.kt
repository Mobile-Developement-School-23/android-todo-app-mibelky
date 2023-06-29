package ru.mobiledevschool.todoapp.newItemFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.mobiledevschool.todoapp.repo.LiveDataRepository
import ru.mobiledevschool.todoapp.repo.ToDoItem
import java.util.Calendar
import java.util.Date
import kotlin.random.Random

class NewItemViewModel(private val repo: LiveDataRepository) : ViewModel() {

    private val _item = MutableLiveData<ToDoItem?>(null)
    val item: LiveData<ToDoItem?> = _item

    private val _priority = MutableLiveData(ToDoItem.Priority.NORMAL)
    val priority: LiveData<ToDoItem.Priority> = _priority

    private val _highlightDeleteButton = MutableLiveData(false)
    val enableDelete: LiveData<Boolean> = _highlightDeleteButton

    private val _date = MutableLiveData<Long>()
    val date: LiveData<Long> = _date

    private val _saveEvent = MutableLiveData<Boolean>(false)
    val saveEvent: LiveData<Boolean> = _saveEvent

    fun getItemById(id: String) = viewModelScope.launch {
        _item.value = repo.getItemById(id)
    }

    fun deleteItemById() = viewModelScope.launch {
        item.value?.let {
            repo.deleteItemById(it.id.toInt())
        }
    }

    fun addNewItem(text: String) {
        val calendar = Calendar.getInstance()
        val newItem =
            ToDoItem(
                text = text,
                completed = false,
                priority = priority.value ?: ToDoItem.Priority.NORMAL,
                deadLine = Date(date.value ?: calendar.timeInMillis),
                creationDate = Date(calendar.timeInMillis),
                editionDate = Date(calendar.timeInMillis)
            )
        viewModelScope.launch { repo.addItem(newItem) }
    }

    fun updateById(text: String) {
        //TODO
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

    class Factory(private val repo: LiveDataRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NewItemViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return NewItemViewModel(repo) as T
            }
            throw IllegalArgumentException("Unable to construct viewModel")
        }
    }
}