package ru.mobiledevschool.todoapp.remote

import com.google.gson.annotations.SerializedName

data class NetworkItemsListResponse(
    @SerializedName("status")
    val status: String?,
    @SerializedName("list")
    val list: List<NetworkItem>?,
    @SerializedName("revision")
    val revision: Int?
)