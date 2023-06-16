package ru.mobiledevschool.todoapp.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import kotlinx.coroutines.flow.asFlow
import java.time.LocalDate

class LiveDataRepository {
    private val _toDoList = MutableLiveData<List<ToDoItem>>()
    val toDoList: LiveData<List<ToDoItem>> = _toDoList

    private val arrayList = ArrayList<ToDoItem>()

    private val _doneSize = MutableLiveData<Int>()
    val doneSize: LiveData<Int> = _doneSize

    private val _showDone = MutableLiveData<Boolean>(true)
    val showDone: LiveData<Boolean> = _showDone

    init {
        arrayList.apply {
            var id = 1
            repeat(4) {
                Log.i("Repository", "items added")
                add(
                    ToDoItem(
                        id++.toString(),
                        "Съешь еще этих мягких французских булок да выпей чаю.",
                        ToDoItem.Priority.LOW, LocalDate.parse("2023-12-12"),
                        false,
                        LocalDate.parse("2023-12-12"),
                        LocalDate.parse("2023-12-12")
                    )
                )
                add(
                    ToDoItem(
                        id++.toString(),
                        "Не тупить целый день на лепре.",
                        ToDoItem.Priority.NORMAL,
                        null,
                        true,
                        LocalDate.parse("2023-12-12"),
                        LocalDate.parse("2023-12-12")
                    )
                )
                add(
                    ToDoItem(
                        id++.toString(),
                        "Родился на улице Герцена, в гастрономе номер двадцать два. Известный экономист, по призванию своему — библиотекарь. В народе — колхозник. В магазине — продавец. В экономике, так сказать, необходим.",
                        ToDoItem.Priority.HIGH,
                        null,
                        false,
                        LocalDate.parse("2023-12-12"),
                        LocalDate.parse("2023-12-12")
                    )
                )
            }
        }
        updateList()
        updateDoneSize()
    }

    fun addToDoItem(newItem: ToDoItem) {
        arrayList.add(newItem)
        _toDoList.value = arrayList
        updateDoneSize()
    }

    fun changeVisibility() {
        _showDone.value = !_showDone.value!!
        updateList()
    }

    private fun updateList() {
        if (_showDone.value!!) _toDoList.value = arrayList.toList()
        else _toDoList.value = arrayList.filter { !it.completed }
    }

    fun deleteItemByPosition(position: Int) {
        arrayList.removeAt(position)
        updateList()
        updateDoneSize()
    }

    fun deleteItemById(id: Int) {
        arrayList.removeIf {  it.id == id.toString() }
        updateList()
        updateDoneSize()
    }

    fun getItemById(id: String): ToDoItem? = _toDoList.value?.find { it.id == id }

    fun getAll() = MutableLiveData(arrayList.toList())

    private fun updateDoneSize() {
        _doneSize.value = arrayList.filter { it.completed }.size
    }
}