package ru.mobiledevschool.todoapp

import java.time.LocalDate

data class ToDoItem(
    val id : String,
    val text : String,
    val priority: Priority,
    val deadLine: LocalDate? = null,
    var completed: Boolean,
    val creationDate: LocalDate,
    val editionDate: LocalDate
) {
    enum class Priority {LOW, NORMAL, HIGH}
}