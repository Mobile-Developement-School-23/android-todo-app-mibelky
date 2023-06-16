package ru.mobiledevschool.todoapp.repo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.mobiledevschool.todoapp.ToDoItem
import java.time.LocalDate


class ToDoItemsRepository {
    private val _toDoList = ArrayList<ToDoItem>()
    private var _showDone = true
    val showDone: Boolean
        get() = _showDone

    val itemsToShow =  { if (_showDone) _toDoList else _toDoList.filter { !it.completed }}
    init {
        _toDoList.apply {
            var id = 1
            repeat(4) {

                add(
                    ToDoItem(
                        id++.toString(),
                        "Съешь еще этих мягких французских булок да выпей чаю.",
                        ToDoItem.Priority.LOW,
                        LocalDate.parse("2023-12-12"),
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
    }

    fun addToDoItem(newItem: ToDoItem) {
        _toDoList.add(newItem)
    }

    fun deleteItemByPosition(position: Int) {
        _toDoList.removeAt(position)
    }

    fun getItems() = _toDoList

    fun changeDoneVisibility() {
        _showDone = !_showDone
    }
}