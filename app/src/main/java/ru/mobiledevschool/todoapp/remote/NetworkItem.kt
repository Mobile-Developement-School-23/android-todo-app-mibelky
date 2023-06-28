package ru.mobiledevschool.todoapp.remote

import com.google.gson.annotations.SerializedName

data class NetworkItem(
    @SerializedName("id")
    val id : String?,
    @SerializedName("text")
    val text : String?,
    @SerializedName("importance")
    val importance: String?,
    @SerializedName("deadline")
    val deadLine: Long?,
    @SerializedName("done")
    val completed: Boolean?,
    @SerializedName("color")
    val color: String?,
    @SerializedName("created_at")
    val createdAt: Long?,
    @SerializedName("changed_at")
    val changedAt: Long?,
    @SerializedName("last_updated_by")
    val lastUpdatedBy: String?
)





