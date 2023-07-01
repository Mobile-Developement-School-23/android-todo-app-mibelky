package ru.mobiledevschool.todoapp.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import ru.mobiledevschool.todoapp.dto.asDomainModel
import ru.mobiledevschool.todoapp.local.ToDoItemsDao
import ru.mobiledevschool.todoapp.remote.ItemsApi
import ru.mobiledevschool.todoapp.remote.NetworkItemRequestContainer
import ru.mobiledevschool.todoapp.remote.Result

class ToDoRepositoryImpl(
    private val toDoItemsDao: ToDoItemsDao,
    private val itemsApi: ItemsApi,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

) : ToDoRepository {

    private var revision = 0

    private val _httpExceptionCodeEvent = MutableLiveData<Int?>(null)
    val httpExceptionCodeEvent: LiveData<Int?>
        get() = _httpExceptionCodeEvent

    fun getAllItems(): Flow<List<ToDoItem>> {
        return toDoItemsDao.getItems().map { it.asDomainModel() }
    }

    private fun startHttpExceptionCodeEvent(code: Int) {
        _httpExceptionCodeEvent.value = code
    }

    fun endHttpExceptionCodeEvent() {
        _httpExceptionCodeEvent.value = null
    }


    override suspend fun addItem(toDoItem: ToDoItem) {
        val networkItem = toDoItem.toNetworkItem()
        val networkItemRequestContainer = NetworkItemRequestContainer(networkItem)
        toDoItemsDao.saveItem(toDoItem.toDTO())
        when (val response = safeApiCall(dispatcher) {
            itemsApi.addItem(
                revision,
                networkItemRequestContainer
            )
        }) {
            is Result.Success ->  updateRevision(response.data.revision)
            is Result.Error -> when (response.exception) {
                    is HttpException -> startHttpExceptionCodeEvent(response.exception.code())
                }
            is Result.Other -> {}
        }
    }

    override suspend fun refreshItems() =
        withContext(dispatcher) {
            val response = itemsApi.getItems()
            updateRevision(response.revision)
            toDoItemsDao.deleteAllItems()
            toDoItemsDao.insertAll(*response.toDTOArray())
        }

    override suspend fun deleteItem(toDoItem: ToDoItem) {
        toDoItemsDao.deleteItemById(toDoItem.id)
        val response = safeApiCall(dispatcher) { itemsApi.deleteItem(revision, toDoItem.id) }
        when (response) {
            is Result.Success -> {
                updateRevision(response.data.revision)
            }

            is Result.Error -> {
                when (response.exception) {
                    is HttpException -> startHttpExceptionCodeEvent(response.exception.code())
                }
            }

            is Result.Other -> {}
        }
    }

    override suspend fun getItemById(id: String): ToDoItem? = withContext(dispatcher) {
        toDoItemsDao.getItemById(id)?.toDomainItem()
    }


    suspend fun getNetworkItemById(id: String): ToDoItem? {
        val response = safeApiCall(dispatcher) { itemsApi.getItem(id) }
        when (response) {
            is Result.Success -> {
                updateRevision(response.data.revision)
                return response.data.element.toDomainItem()
            }

            is Result.Error -> when (response.exception) {
                is HttpException -> {
                    startHttpExceptionCodeEvent(response.exception.code()); return null
                }

                else -> return null
            }

            is Result.Other -> {
                return null
            }
        }
    }

    override suspend fun changeItem(toDoItem: ToDoItem) {
        val networkItem = toDoItem.toNetworkItem()
        val networkItemRequestContainer = NetworkItemRequestContainer(networkItem)
        toDoItemsDao.saveItem(toDoItem.toDTO())

        val response = safeApiCall(dispatcher) {
            itemsApi.updateItem(
                revision,
                toDoItem.id,
                networkItemRequestContainer
            )
        }
        when (response) {
            is Result.Success -> {
                updateRevision(response.data.revision)
            }

            is Result.Error -> {
                when (response.exception) {
                    is HttpException -> startHttpExceptionCodeEvent(response.exception.code())
                }

            }

            is Result.Other -> {
            }
        }
    }

    fun changeVisibility() {
//        _showDone.value = !_showDone.value!!
    }

    private fun updateRevision(value: Int) {
        revision = value
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
