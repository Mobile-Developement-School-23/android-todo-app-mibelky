package ru.mobiledevschool.todoapp.remote.dtobjects

import com.google.gson.annotations.SerializedName

/*
* Класс для парсинга данных из ответа сервера в список дел
 */
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
