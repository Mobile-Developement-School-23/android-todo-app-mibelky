package ru.mobiledevschool.todoapp.remote.dtobjects

import com.google.gson.annotations.SerializedName
import ru.mobiledevschool.todoapp.local.entity.ToDoItemEntity
import ru.mobiledevschool.todoapp.repo.ToDoItem
/*
* Класс представления наших дел для парсинга в JSON и обратно
 */
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
    fun toToDoItemDTO() = ToDoItemEntity(
        id = this.id,
        text = this.text,
        priority = this.importance.toPriority(),
        deadLine = this.deadLine,
        creationDate = this.createdAt,
        editionDate = this.changedAt,
        completed = this.completed
    )

    private fun String.toPriority() = when (this) {
        "low" -> ToDoItem.Priority.LOW
        "important" -> ToDoItem.Priority.HIGH
        else -> ToDoItem.Priority.NORMAL
    }
}







