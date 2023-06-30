package ru.mobiledevschool.todoapp.remote

import com.google.gson.annotations.SerializedName

data class NetworkItemListRequestContainer(
    @SerializedName("list")
    val list: List<NetworkItem>
)
