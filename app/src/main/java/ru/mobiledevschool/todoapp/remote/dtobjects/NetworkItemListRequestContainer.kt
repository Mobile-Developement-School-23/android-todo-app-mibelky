package ru.mobiledevschool.todoapp.remote.dtobjects

import com.google.gson.annotations.SerializedName

/*
* Класс для парсинга данных в запрос со списком дел
 */
data class NetworkItemListRequestContainer(
    @SerializedName("list")
    val list: List<NetworkItem>
)
