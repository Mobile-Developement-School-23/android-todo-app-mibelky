package ru.mobiledevschool.todoapp.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.Date

class LiveDataRepository {
    private val _toDoList = MutableLiveData<List<ToDoItem>>()
    val toDoList: LiveData<List<ToDoItem>> = _toDoList

    private val arrayList = ArrayList<ToDoItem>()

    private val _doneSize = MutableLiveData<Int>()
    val doneSize: LiveData<Int> = _doneSize

    private val _showDone = MutableLiveData<Boolean>(true)
    val showDone: LiveData<Boolean> = _showDone

    private lateinit var remoteList: List<ToDoItem>

    /*    suspend fun getItems() {
        remoteList = Api.retrofitService.getItems().list
    }*/

    init {

        arrayList.apply {
            var id = 1
            repeat(4) {
                Log.i("Repository", "items added")
                add(
                    ToDoItem(
                        id++.toString(),
                        "Съешь еще этих мягких французских булок да выпей чаю.",
                        ToDoItem.Priority.LOW, Date(1686969422L),
                        false,
                        Date(1686969422L),
                        Date(1686969422L)
                    )
                )
                add(
                    ToDoItem(
                        id++.toString(),
                        "Не тупить целый день на лепре.",
                        ToDoItem.Priority.NORMAL,
                        null,
                        true,
                        Date(1686969422L),
                        Date(1686969422L)
                    )
                )
                add(
                    ToDoItem(
                        id++.toString(),
                        "Родился на улице Герцена, в гастрономе номер двадцать два. Известный экономист, по призванию своему — библиотекарь. В народе — колхозник. В магазине — продавец. В экономике, так сказать, необходим.",
                        ToDoItem.Priority.HIGH,
                        null,
                        false,
                        Date(1686969422L),
                        Date(1686969422L)
                    )
                )
            }
        }
        updateList()
        updateDoneSize()
    }

    fun addToDoItem(newItem: ToDoItem) {
        arrayList.add(newItem)
        updateList()
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

    fun deleteItemById(id: Int) {
        arrayList.removeIf {  it.id == id.toString() }
        updateList()
        updateDoneSize()
    }

    fun checkItemById(id: Int) {
        val item = arrayList.find {  it.id == id.toString() }
        item?.completed = !item?.completed!!
        if (!_showDone.value!!) updateList()
        updateDoneSize()
    }

    fun getItemById(id: String): ToDoItem? = arrayList.find {  it.id == id }

    private fun updateDoneSize() {
        _doneSize.value = arrayList.filter { it.completed }.size
    }
}