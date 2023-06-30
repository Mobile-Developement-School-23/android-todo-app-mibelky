package ru.mobiledevschool.todoapp.remote

import androidx.lifecycle.MutableLiveData
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import ru.mobiledevschool.todoapp.remote.Result

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
        @Path("id") id: String,
        item: NetworkItemRequestContainer
    ): NetworkItemResponseContainer

    @DELETE("list/{id}")
    suspend fun deleteItem(
        @Header("X-Last-Known-Revision") revision: Int,
        @Path("id") id: String
    ): NetworkItemResponseContainer

}