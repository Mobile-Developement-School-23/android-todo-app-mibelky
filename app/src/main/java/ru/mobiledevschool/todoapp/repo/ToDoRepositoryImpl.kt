package ru.mobiledevschool.todoapp.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.mobiledevschool.todoapp.dto.asDomainModel
import ru.mobiledevschool.todoapp.local.ToDoItemsDao
import ru.mobiledevschool.todoapp.remote.ItemsApi
import ru.mobiledevschool.todoapp.remote.NetworkItemRequestContainer

class ToDoRepositoryImpl(
    private val itemsDao: ToDoItemsDao,
    private val itemsApi: ItemsApi,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

) : ToDoRepository {

    private var revision = 0

    private val _exceptionMessageEvent = MutableLiveData<String?>(null)
    val exceptionMessageEvent: LiveData<String?>
        get() = _exceptionMessageEvent

    fun getAllItems(): Flow<List<ToDoItem>> {
        return itemsDao.getItems().map { it.asDomainModel() }
    }

    fun getDoneQuantity(): Flow<Int> = itemsDao.getDoneQuantity()

    private fun startExceptionMessageEvent(message: String) {
        _exceptionMessageEvent.value = message
    }

    fun endExceptionMessageEvent() {
        _exceptionMessageEvent.value = null
    }


    override suspend fun addItem(toDoItem: ToDoItem) {
        val container = NetworkItemRequestContainer(toDoItem.toNetworkItem())
        safeCall { itemsApi.addItem(revision, container) }.unpack()?.let {
            safeCall { itemsDao.saveItem(toDoItem.toDTO()) }
            updateRevision(it.revision)
        }
    }

    override suspend fun refreshItems() {
        safeCall { itemsApi.getItems() }.unpack()?.let {
            updateRevision(it.revision)
            safeCall { itemsDao.deleteAllItems() }.unpack()
            safeCall { itemsDao.insertAll(*it.toDTOArray()) }.unpack()
        }
    }

    override suspend fun deleteItem(toDoItem: ToDoItem) {
        val result = safeCall { itemsApi.deleteItem(revision, toDoItem.id) }
        result.unpack()?.let {
            updateRevision(it.revision)
            safeCall { itemsDao.deleteItemById(toDoItem.id) }.unpack()
        }
    }

    override suspend fun getItemById(id: String): ToDoItem? {
        return safeCall { itemsDao.getItemById(id) }.unpack()?.toDomainItem()
    }

    suspend fun getNetworkItemById(id: String): ToDoItem? {
        val result = safeCall { itemsApi.getItem(id) }
        return result.unpack()?.let {
            updateRevision(it.revision)
            it.element.toDomainItem()
        }
    }

    override suspend fun changeItem(toDoItem: ToDoItem) {
        val container = NetworkItemRequestContainer(toDoItem.toNetworkItem())
        val result = safeCall {
            itemsApi.updateItem(revision, toDoItem.id, container)
        }
        result.unpack()?.let {
            withContext(dispatcher) { itemsDao.saveItem(toDoItem.toDTO()) }
            updateRevision(it.revision)
        }
    }

    fun changeVisibility() {
//        _showDone.value = !_showDone.value!!
    }

    private fun updateRevision(value: Int) {
        revision = value
    }

    private suspend fun <T> safeCall(
        call: suspend () -> T
    ): Result<T> = withContext(dispatcher) {
        runCatching { call.invoke() }
    }

    private fun <T> Result<T>.unpack(): T? = exceptionOrNull()?.let { exception ->
        startExceptionMessageEvent(messageFrom(exception))
        null
    } ?: getOrNull()
}
