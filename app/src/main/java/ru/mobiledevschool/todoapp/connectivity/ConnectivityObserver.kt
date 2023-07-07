package ru.mobiledevschool.todoapp.connectivity

import kotlinx.coroutines.flow.Flow

/*
* Интерфейс для отслеживания состояния сети
 */
interface ConnectivityObserver {

    fun observe(): Flow<Status>

    enum class Status {
        Available, Unavailable, Losing, Lost;
    fun isAvailable() = this == Available
    }
}