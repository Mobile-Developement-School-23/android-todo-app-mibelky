package ru.mobiledevschool.todoapp.remote

import com.google.gson.annotations.SerializedName
import java.util.Date

data class NetworkItem(
    @SerializedName("id")
    val id : String?,
    @SerializedName("text")
    val text : String?,
    @SerializedName("importance")
    val importance: String?,
    @SerializedName("deadline")
    val deadLine: Date?,
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

data class NetworkItemsListResponse(
    @SerializedName("status")
    val status: String?,
    @SerializedName("list")
    val list: List<NetworkItem>?,
    @SerializedName("revision")
    val revision: Int?
)

