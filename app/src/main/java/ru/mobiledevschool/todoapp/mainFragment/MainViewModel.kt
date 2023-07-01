package ru.mobiledevschool.todoapp.mainFragment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import retrofit2.HttpException
import ru.mobiledevschool.todoapp.repo.ToDoItem
import ru.mobiledevschool.todoapp.repo.ToDoRepositoryImpl
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.Date

class MainViewModel(private val repo: ToDoRepositoryImpl) : ViewModel() {

    val showDone = repo.showDone

    val listToShow = MutableLiveData<List<ToDoItem>>()

    val httpExceptionCodeEvent = repo.httpExceptionCodeEvent
    fun endHttpExceptionCodeEvent() = repo.endHttpExceptionCodeEvent()

    val doneQuantity = MutableLiveData<Int>()

    init {
        viewModelScope.launch {
            try {
                repo.getAllItems().collect { items ->
                    listToShow.postValue(items)
                    doneQuantity.postValue(items.count { it.completed })
                }
                repo.refreshItems()
            } catch (e: UnknownHostException) { // Handle no internet
                Log.e("ViewModel", "Unknown host exception: $e");
            } catch (e: HttpException) { // Handle HTTP errors (like 400s, 500s)
                Log.e("ViewModel", "Http Exception: $e");
            } catch (e: IOException) { // Handle any other I/O errors
                Log.e("ViewModel", "IO exception: $e");
            } catch (e: SocketTimeoutException) { // Handle request timeout Exception
                Log.e("ViewModel", "Socket timeout exception: $e");
            }

        }
    }

    private fun refreshItems() = viewModelScope.launch {
        repo.refreshItems()
    }


    fun deleteItem(toDoItem: ToDoItem) = viewModelScope.launch {
        try {
            repo.deleteItem(toDoItem)
        } catch (e: UnknownHostException) { // Handle no internet
            Log.e("ViewModel", "Unknown host exception: $e");
        } catch (e: HttpException) { // Handle HTTP errors (like 400s, 500s)
            Log.e("ViewModel", "Http Exception: $e");
        } catch (e: IOException) { // Handle any other I/O errors
            Log.e("ViewModel", "IO exception: $e");
        } catch (e: SocketTimeoutException) { // Handle request timeout Exception
            Log.e("ViewModel", "Socket timeout exception: $e");
        }
    }


    fun checkItem(toDoItem: ToDoItem) = viewModelScope.launch {

        val checkedItem = ToDoItem(
            id = toDoItem.id,
            text = toDoItem.text,
            completed = !toDoItem.completed,
            priority = toDoItem.priority,
            deadLine = toDoItem.deadLine,
            creationDate = toDoItem.creationDate,
            editionDate = toDoItem.editionDate
        )

        try {
            repo.changeItem(checkedItem)
        } catch (e: UnknownHostException) { // Handle no internet
            Log.e("ViewModel", "Unknown host exception: $e");
        } catch (e: HttpException) { // Handle HTTP errors (like 400s, 500s)
            Log.e("ViewModel", "Http Exception: $e");
        } catch (e: IOException) { // Handle any other I/O errors
            Log.e("ViewModel", "IO exception: $e");
        } catch (e: SocketTimeoutException) { // Handle request timeout Exception
            Log.e("ViewModel", "Socket timeout exception: $e");
        }
    }

    fun changeVisibility() {
        repo.changeVisibility()
    }

    class Factory(private val repo: ToDoRepositoryImpl) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(repo) as T
            }
            throw IllegalArgumentException("Unable to construct viewModel")
        }
    }

}
