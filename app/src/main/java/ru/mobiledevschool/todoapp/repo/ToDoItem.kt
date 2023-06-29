package ru.mobiledevschool.todoapp.repo

import ru.mobiledevschool.todoapp.remote.NetworkItem
import java.util.Date


data class ToDoItem(
    val id: String,
    var text: String,
    var priority: Priority,
    var deadLine: Date? = null,
    var completed: Boolean,
    val creationDate: Date,
    val editionDate: Date?
) {
    enum class Priority {
        LOW, NORMAL, HIGH;

        override fun toString() = when (this) {
            LOW -> "low"
            HIGH -> "important"
            NORMAL -> "basic"
        }
    }

    fun toNetworkItem() = NetworkItem(
        id = this.id,
        text = this.text,
        importance = this.priority.toString(),
        deadLine = this.deadLine?.time,
        completed = this.completed,
        createdAt = this.creationDate.time,
        changedAt = this.editionDate?.time ?: 0

    )
}