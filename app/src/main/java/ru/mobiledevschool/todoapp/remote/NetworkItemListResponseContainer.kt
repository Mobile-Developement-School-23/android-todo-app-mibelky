package ru.mobiledevschool.todoapp.remote

import com.google.gson.annotations.SerializedName

data class NetworkItemListResponseContainer(
    @SerializedName("status")
    val status: String,
    @SerializedName("list")
    val list: List<NetworkItem>?,
    @SerializedName("revision")
    val revision: Int
) {
    fun toDTOArray() = list?.map { it.toToDoItemDTO() }?.toTypedArray() ?: emptyArray()
}
