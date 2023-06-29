package ru.mobiledevschool.todoapp.remote

import com.google.gson.annotations.SerializedName
import ru.mobiledevschool.todoapp.repo.ToDoItem
import ru.mobiledevschool.todoapp.utility.toDateFormat
import java.util.Date

data class NetworkItem(
    @SerializedName("id")
    val id: String,
    @SerializedName("text")
    val text: String,
    @SerializedName("importance")
    val importance: String,
    @SerializedName("deadline")
    val deadLine: Long?,
    @SerializedName("done")
    val completed: Boolean,
    @SerializedName("color")
    val color: String? = null,
    @SerializedName("created_at")
    val createdAt: Long,
    @SerializedName("changed_at")
    val changedAt: Long,
    @SerializedName("last_updated_by")
    val lastUpdatedBy: String? = "null"
) {
    fun toDomainItem() = ToDoItem(
        id = this.id,
        text = this.text,
        priority = this.importance.toPriority(),
        deadLine = this.deadLine?.let {Date(it)},
        completed = this.completed,
        creationDate = Date(this.createdAt),
        editionDate = Date(this.changedAt)
    )

    private fun String.toPriority() = when (this) {
        "low" -> ToDoItem.Priority.LOW
        "important" -> ToDoItem.Priority.HIGH
        else -> ToDoItem.Priority.NORMAL
    }
}







