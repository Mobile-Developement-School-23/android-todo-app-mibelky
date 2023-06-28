package ru.mobiledevschool.todoapp.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.mobiledevschool.todoapp.remote.ItemsApi
import ru.mobiledevschool.todoapp.remote.AuthInterceptor
import java.util.Date

class LiveDataRepository() {

    lateinit var itemsApi: ItemsApi

    private val _toDoList = MutableLiveData<List<ToDoItem>>()
    val toDoList: LiveData<List<ToDoItem>> = _toDoList

    private val arrayList = ArrayList<ToDoItem>()

    private val _doneSize = MutableLiveData<Int>()
    val doneSize: LiveData<Int> = _doneSize

    private val _showDone = MutableLiveData<Boolean>(true)
    val showDone: LiveData<Boolean> = _showDone

    suspend fun getItems() {
        //remoteList = itemsApi.getItems().value.list
    }

    suspend fun getSize() {
        val remoteObject = itemsApi.getItems().value.toString()
        Log.w("Remote", "$remoteObject")
    }
    init {
        configureRetrofit()
        arrayList.apply {
            var id = 1
            repeat(4) {
                Log.i("Repository", "items added")
                add(
                    ToDoItem(
                        id++.toString(),
                        "Съешь еще этих мягких французских булок да выпей чаю.",
                        ToDoItem.Priority.LOW, Date(1686969422L),
                        false,
                        Date(1686969422L),
                        Date(1686969422L)
                    )
                )
                add(
                    ToDoItem(
                        id++.toString(),
                        "Не тупить целый день на лепре.",
                        ToDoItem.Priority.NORMAL,
                        null,
                        true,
                        Date(1686969422L),
                        Date(1686969422L)
                    )
                )
                add(
                    ToDoItem(
                        id++.toString(),
                        "Родился на улице Герцена, в гастрономе номер двадцать два. Известный экономист, по призванию своему — библиотекарь. В народе — колхозник. В магазине — продавец. В экономике, так сказать, необходим.",
                        ToDoItem.Priority.HIGH,
                        null,
                        false,
                        Date(1686969422L),
                        Date(1686969422L)
                    )
                )
            }
        }
        updateList()
        updateDoneSize()
    }

    fun addToDoItem(newItem: ToDoItem) {
        arrayList.add(newItem)
        updateList()
        updateDoneSize()
    }

    fun changeVisibility() {
        _showDone.value = !_showDone.value!!
        updateList()
    }

    private fun updateList() {
        if (_showDone.value!!) _toDoList.value = arrayList.toList()
        else _toDoList.value = arrayList.filter { !it.completed }
    }

    fun deleteItemById(id: Int) {
        arrayList.removeIf { it.id == id.toString() }
        updateList()
        updateDoneSize()
    }

    fun checkItemById(id: Int) {
        val item = arrayList.find { it.id == id.toString() }
        item?.completed = !item?.completed!!
        if (!_showDone.value!!) updateList()
        updateDoneSize()
    }

    fun getItemById(id: String): ToDoItem? = arrayList.find { it.id == id }

    private fun updateDoneSize() {
        _doneSize.value = arrayList.filter { it.completed }.size
    }

    private fun configureRetrofit() {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            //.addInterceptor(AuthInterceptor())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://beta.mrdekk.ru/todobackend/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        itemsApi = retrofit.create(ItemsApi::class.java)
    }
}