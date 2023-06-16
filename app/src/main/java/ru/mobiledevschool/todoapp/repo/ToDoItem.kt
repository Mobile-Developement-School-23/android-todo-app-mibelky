package ru.mobiledevschool.todoapp.repo

import java.time.LocalDate

data class ToDoItem(
    val id : String,
    var text : String,
    var priority: Priority,
    var deadLine: LocalDate? = null,
    var completed: Boolean,
    val creationDate: LocalDate,
    val editionDate: LocalDate
) {
    enum class Priority {LOW, NORMAL, HIGH}
}