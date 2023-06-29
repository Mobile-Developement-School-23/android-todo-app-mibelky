package ru.mobiledevschool.todoapp.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.mobiledevschool.todoapp.remote.ItemsApi
import ru.mobiledevschool.todoapp.remote.AuthInterceptor
import ru.mobiledevschool.todoapp.remote.NetworkItem
import ru.mobiledevschool.todoapp.remote.NetworkItemRequestContainer
import java.util.UUID
import java.util.concurrent.TimeUnit

class LiveDataRepository : ToDoRepository {

    lateinit var itemsApi: ItemsApi

    private var revision = 0

    private val _toDoList = MutableLiveData<List<ToDoItem>>()
    val toDoList: LiveData<List<ToDoItem>> = _toDoList

    private val arrayList = ArrayList<ToDoItem>()

    val doneSize: LiveData<Int>
        get() = _remoteList.map { it.count { item -> item.completed } }

    private val _showDone = MutableLiveData(true)
    val showDone: LiveData<Boolean> = _showDone

    private val _remoteList = MutableLiveData<List<ToDoItem>>(emptyList<ToDoItem>())
    val remoteList: LiveData<List<ToDoItem>> = _remoteList


    suspend fun addItem(item: ToDoItem) {
        val networkItem = item.toNetworkItem()
        val networkItemRequestContainer = NetworkItemRequestContainer(networkItem)
        val response = itemsApi.addItem(revision,
            networkItemRequestContainer)
    }

    init {
        configureRetrofit()
    }

    override suspend fun refreshItems() {
        val response = itemsApi.getItems()
        _remoteList.value = response.list?.map { it.toDomainItem() }
        updateRevision(response.revision)
    }

    suspend fun deleteItemById(id: Int) {
        val response = itemsApi.deleteItem(revision, id.toString())
        updateRevision(response.revision)
        refreshItems()
    }

    suspend fun getItemById(id: String): ToDoItem {
        val response = itemsApi.getItem(id)
        updateRevision(response.revision)
        return response.element.toDomainItem()
    }

    private fun updateRevision(value: Int) {
        revision = value
    }

    fun addToDoItem(newItem: ToDoItem) {
        arrayList.add(newItem)
        updateList()
    }

    fun changeVisibility() {
        _showDone.value = !_showDone.value!!
        updateList()
    }

    private fun updateList() {
        if (_showDone.value!!) _toDoList.value = arrayList.toList()
        else _toDoList.value = arrayList.filter { !it.completed }
    }

    fun checkItemById(id: Int) {
        val item = arrayList.find { it.id == id.toString() }
        item?.completed = !item?.completed!!
        if (!_showDone.value!!) updateList()
    }

    private fun configureRetrofit() {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
            .callTimeout(0, TimeUnit.MILLISECONDS)
            .connectTimeout(15000, TimeUnit.MILLISECONDS)
            .readTimeout(15000, TimeUnit.MILLISECONDS)
            .writeTimeout(15000, TimeUnit.MILLISECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(AuthInterceptor())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://beta.mrdekk.ru/todobackend/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        itemsApi = retrofit.create(ItemsApi::class.java)
    }
}