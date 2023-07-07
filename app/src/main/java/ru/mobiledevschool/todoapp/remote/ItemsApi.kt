package ru.mobiledevschool.todoapp.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import ru.mobiledevschool.todoapp.remote.dtobjects.NetworkItemListRequestContainer
import ru.mobiledevschool.todoapp.remote.dtobjects.NetworkItemListResponseContainer
import ru.mobiledevschool.todoapp.remote.dtobjects.NetworkItemRequestContainer
import ru.mobiledevschool.todoapp.remote.dtobjects.NetworkItemResponseContainer

/*
* Интерфейс для нашего API, содержит описание функций для последующего формирования запросов с
* помощью Retrofit
 */
interface ItemsApi {

    @GET("list/")
    suspend fun getItems(): NetworkItemListResponseContainer

    @PATCH("list/")
    suspend fun updateItems(
        @Header("X-Last-Known-Revision") revision: Int,
        list: NetworkItemListRequestContainer
    ): NetworkItemListResponseContainer

    @GET("list/{id}/")
    suspend fun getItem(@Path("id") id: String): NetworkItemResponseContainer

    @POST("list/")
    suspend fun addItem(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body item: NetworkItemRequestContainer
    ): NetworkItemResponseContainer

    @PUT("list/{id}")
    suspend fun updateItem(
        @Header("X-Last-Known-Revision") revision: Int,
        @Path("id") id: String,
        @Body item: NetworkItemRequestContainer
    ): NetworkItemResponseContainer

    @DELETE("list/{id}")
    suspend fun deleteItem(
        @Header("X-Last-Known-Revision") revision: Int,
        @Path("id") id: String
    ): NetworkItemResponseContainer

}