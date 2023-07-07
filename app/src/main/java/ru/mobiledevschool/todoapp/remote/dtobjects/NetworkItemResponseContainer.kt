package ru.mobiledevschool.todoapp.remote.dtobjects

import com.google.gson.annotations.SerializedName
/*
* Класс для парсинга данных из ответа сервера в одно дело
 */
data class NetworkItemResponseContainer(
    @SerializedName("status")
    val status: String,
    @SerializedName("element")
    val element: NetworkItem,
    @SerializedName("revision")
    val revision: Int
)