package ru.mobiledevschool.todoapp.remote

import androidx.lifecycle.MutableLiveData
import retrofit2.http.GET
import retrofit2.http.Headers

interface ItemsApi {

    @GET("list/")
    @Headers("Authorization: Bearer clagging")
    suspend fun getItems(): MutableLiveData<NetworkItemsListResponse>
}