package ru.mobiledevschool.todoapp.repo

import java.util.Date

data class ToDoItem(
    val id : String,
    var text : String,
    var priority: Priority,
    var deadLine: Date? = null,
    var completed: Boolean,
    val creationDate: Date,
    val editionDate: Date?
) {
    enum class Priority {LOW, NORMAL, HIGH}
}