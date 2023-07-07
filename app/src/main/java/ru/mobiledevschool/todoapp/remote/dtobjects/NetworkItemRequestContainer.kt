package ru.mobiledevschool.todoapp.remote.dtobjects

import com.google.gson.annotations.SerializedName

/*
* Класс для парсинга данных в запрос со одним делом
 */
data class NetworkItemRequestContainer(
    @SerializedName("element")
    val list: NetworkItem
)