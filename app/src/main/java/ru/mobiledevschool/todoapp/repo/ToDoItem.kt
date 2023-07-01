package ru.mobiledevschool.todoapp.repo

import ru.mobiledevschool.todoapp.dto.ToDoItemDTO
import ru.mobiledevschool.todoapp.remote.NetworkItem
import java.util.Date
import java.util.UUID


data class ToDoItem(
    val id: String = UUID.randomUUID().toString(),
    var text: String,
    var completed: Boolean,
    var priority: Priority,
    var deadLine: Date? = null,
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

    fun toDTO() = ToDoItemDTO(
        id = this.id,
        text = this.text,
        priority = this.priority,
        deadLine = this.deadLine?.time,
        completed = this.completed,
        creationDate = this.creationDate.time,
        editionDate = this.editionDate?.time ?: 0
    )
}