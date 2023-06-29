package ru.mobiledevschool.todoapp.remote

import com.google.gson.annotations.SerializedName

data class NetworkItemRequestContainer(
    @SerializedName("element")
    val list: NetworkItem
)