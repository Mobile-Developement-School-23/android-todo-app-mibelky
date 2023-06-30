package ru.mobiledevschool.todoapp.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.mobiledevschool.todoapp.remote.AuthInterceptor
import ru.mobiledevschool.todoapp.remote.ItemsApi
import ru.mobiledevschool.todoapp.remote.NetworkItemRequestContainer
import ru.mobiledevschool.todoapp.remote.Result
import java.util.concurrent.TimeUnit

class ToDoRepositoryImpl : ToDoRepository {

    private val dispatcher = Dispatchers.IO

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

    private val _httpExceptionCodeEvent = MutableLiveData<Int?>(null)
    val httpExceptionCodeEvent: LiveData<Int?>
        get() = _httpExceptionCodeEvent
    private fun startHttpExceptionCodeEvent(code: Int) { _httpExceptionCodeEvent.value = code }
    fun endHttpExceptionCodeEvent() { _httpExceptionCodeEvent.value = null }
    init {
        configureRetrofit()
    }


    override suspend fun addItem(toDoItem: ToDoItem) {
        val networkItem = toDoItem.toNetworkItem()
        val networkItemRequestContainer = NetworkItemRequestContainer(networkItem)

        when (val response = safeApiCall(dispatcher) {
            itemsApi.addItem(
                revision,
                networkItemRequestContainer
            )
        }) {
            is Result.Success -> {
                updateRevision(response.data.revision)
                refreshItems()
                //Do something with the responce if needed response.data.element.toDomainItem()
            }

            is Result.Error -> {
                startHttpExceptionCodeEvent((response.exception as HttpException).code())
            }
            is Result.Other -> {refreshItems()}
        }
    }

    override suspend fun refreshItems() {
        val response = safeApiCall(dispatcher) { itemsApi.getItems() }
        when (response) {
            is Result.Success -> {
                updateRevision(response.data.revision)
                _remoteList.value = response.data.list?.map { it.toDomainItem() }
            }

            is Result.Error -> {
                startHttpExceptionCodeEvent((response.exception as HttpException).code())
            }
            is Result.Other -> {}
        }
    }


    override suspend fun deleteItemById(id: String) {
        val response = safeApiCall(dispatcher) { itemsApi.deleteItem(revision, id) }
        when (response) {
            is Result.Success -> {
                updateRevision(response.data.revision)
                refreshItems()
            }

            is Result.Error -> {
                startHttpExceptionCodeEvent((response.exception as HttpException).code())
            }
            is Result.Other -> {}
        }
    }

    override suspend fun getItemById(id: String): ToDoItem? {
        val response = safeApiCall(dispatcher) { itemsApi.getItem(id) }
        when (response) {
            is Result.Success -> {
                updateRevision(response.data.revision)
                return response.data.element.toDomainItem()
            }

            is Result.Error -> {
                startHttpExceptionCodeEvent((response.exception as HttpException).code())
                return null
            }
            is Result.Other -> {return null}
        }
    }

    private fun updateRevision(value: Int) {
        revision = value
    }

    override suspend fun changeItem(toDoItem: ToDoItem) {
        TODO("Not yet implemented")
    }

    fun changeVisibility() {
        _showDone.value = !_showDone.value!!
        updateList()
    }

    private fun updateList() {
        if (_showDone.value!!) _toDoList.value = arrayList.toList()
        else _toDoList.value = arrayList.filter { !it.completed }
    }

    fun checkItemById(id: String) {
        val item = arrayList.find { it.id == id }
        //GET ITEM AND UPDATE
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

    private suspend fun <T> safeApiCall(
        dispatcher: CoroutineDispatcher,
        apiCall: suspend () -> T
    ): Result<T> {
        return withContext(dispatcher) {
            try {
                Result.Success(apiCall.invoke())
            } catch (exception: Exception) {
                Result.Error(exception)
            }
        }
    }
}
