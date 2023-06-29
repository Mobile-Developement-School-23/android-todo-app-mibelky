package ru.mobiledevschool.todoapp.remote

import com.google.gson.annotations.SerializedName

data class NetworkItemResponseContainer(
    @SerializedName("status")
    val status: String,
    @SerializedName("element")
    val element: NetworkItem,
    @SerializedName("revision")
    val revision: Int
)