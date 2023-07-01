package ru.mobiledevschool.todoapp.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.mobiledevschool.todoapp.remote.NetworkItem
import ru.mobiledevschool.todoapp.repo.ToDoItem
import java.util.Date
import java.util.UUID

@Entity(tableName = "toDoItems")
data class ToDoItemDTO(
    @PrimaryKey @ColumnInfo(name = "id") val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "text") var text: String,
    @ColumnInfo(name = "completed") var completed: Boolean,
    @ColumnInfo(name = "priority") var priority: ToDoItem.Priority,
    @ColumnInfo(name = "deadLine") var deadLine: Long? = null,
    @ColumnInfo(name = "creationDate") var creationDate: Long,
    @ColumnInfo(name = "editionDate") var editionDate: Long?
) {
    fun toDomainItem(): ToDoItem {
        return ToDoItem(
            id = this.id,
            text = this.text,
            completed = this.completed,
            priority = this.priority,
            deadLine = this.deadLine?.let { Date(it) },
            creationDate = Date(this.creationDate),
            editionDate = this.editionDate?.let { Date(it) }
        )
    }

    fun toNetworkItem(): NetworkItem {
        return NetworkItem(
            id = this.id,
            text = this.text,
            completed = this.completed,
            importance = this.priority.toString(),
            deadLine = this.deadLine,
            createdAt = this.creationDate,
            changedAt = this.editionDate ?: this.creationDate
        )
    }
}

fun List<ToDoItemDTO>.asDomainModel(): List<ToDoItem> {
    return this.map { it.toDomainItem() }
}