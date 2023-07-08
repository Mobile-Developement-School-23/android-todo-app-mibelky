package ru.mobiledevschool.todoapp.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import ru.mobiledevschool.todoapp.connectivity.NetworkConnectivityObserver
import ru.mobiledevschool.todoapp.di.AppScope
import ru.mobiledevschool.todoapp.local.entity.asDomainModel
import ru.mobiledevschool.todoapp.local.ToDoItemsDao
import ru.mobiledevschool.todoapp.remote.ItemsApi
import ru.mobiledevschool.todoapp.remote.dtobjects.NetworkItem
import ru.mobiledevschool.todoapp.remote.dtobjects.NetworkItemListRequestContainer
import ru.mobiledevschool.todoapp.remote.dtobjects.NetworkItemListResponseContainer
import ru.mobiledevschool.todoapp.remote.dtobjects.NetworkItemRequestContainer
import java.lang.Exception
import javax.inject.Inject

/*
* Единственный репозиторий нашего приложения, управляет данными из БД и удаленного сервиса, готовит
* их для ViewModel
 */
@AppScope
class ToDoRepositoryImpl @Inject constructor(
    private val itemsDao: ToDoItemsDao,
    private val itemsApi: ItemsApi,
    private val networkConnectivityObserver: NetworkConnectivityObserver,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val appScope: CoroutineScope
) : ToDoRepository {

    //private val dispatcher: CoroutineDispatcher = Dispatchers.IO
    private var revision = 0

    private val _exceptionMessageEvent = MutableLiveData<String?>(null)
    val exceptionMessageEvent: LiveData<String?>
        get() = _exceptionMessageEvent

    private var showDone = true

    init {
        networkStatusObserving()
    }

    private fun networkStatusObserving() {
        appScope.launch {
            networkConnectivityObserver.observe().collect {
                if (it.isAvailable()) {
                    refreshItems()
                }
            }
        }
    }

    fun getAllItems(): Flow<List<ToDoItem>> =
        itemsDao.getItems().map { it.asDomainModel() }.flowOn(dispatcher)

    fun getDoneQuantity(): Flow<Int> = itemsDao.getDoneQuantity().flowOn(dispatcher)

    private fun startExceptionMessageEvent(message: String) {
        _exceptionMessageEvent.value = message
    }

    fun endExceptionMessageEvent() {
        _exceptionMessageEvent.value = null
    }


    override suspend fun addItem(toDoItem: ToDoItem) {
        itemsDao.saveItem(toDoItem.toDTO())
        val container = NetworkItemRequestContainer(toDoItem.toNetworkItem())
        safeCall { itemsApi.addItem(revision, container) }
            .unpack()
            ?.let { updateRevision(it.revision) }
    }

    override suspend fun refreshItems() {
        safeCall { itemsApi.getItems() }.unpack()?.let {
            updateRevision(it.revision)
            withContext(dispatcher) {
                itemsDao.deleteAllItems()
                itemsDao.insertAll(*it.toEntityArray())
            }
        }
    }


    override suspend fun deleteItem(toDoItem: ToDoItem) {
        itemsDao.deleteItemById(toDoItem.id)
        safeCall { itemsApi.deleteItem(revision, toDoItem.id) }.unpack()?.let {
            updateRevision(it.revision)
        }
    }

    override suspend fun getItemById(id: String): ToDoItem? =
        itemsDao.getItemById(id)?.toDomainItem()

    override suspend fun changeItem(toDoItem: ToDoItem) {
        itemsDao.saveItem(toDoItem.toDTO())
        val container = NetworkItemRequestContainer(toDoItem.toNetworkItem())
        safeCall { itemsApi.updateItem(revision, toDoItem.id, container) }.unpack()
            ?.let { updateRevision(it.revision) }
    }

    fun changeVisibility() {
        showDone = !showDone
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
    appScope.launch { sync(exception) }
        null
    } ?: getOrNull()

    private suspend fun sync(exception: Throwable) = withContext(dispatcher) {
        if (exception is HttpException && exception.code() in setOf(400, 404)) {
            refreshItems()
        }
    }
}
