package ru.mobiledevschool.todoapp.repo

import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

    fun messageFrom(exception: Throwable): String = when (exception) {
        is SocketTimeoutException -> ExceptionMessage.SOCKET_TIMEOUT.message
        is IOException -> ExceptionMessage.IO.message
        is HttpException -> {
            when (exception.code()) {
                400 -> ExceptionMessage.HTTP_400.message
                401 -> ExceptionMessage.HTTP_401.message
                404 -> ExceptionMessage.HTTP_404.message
                500 -> ExceptionMessage.HTTP_500.message
                else -> ExceptionMessage.UNKNOWN.message
            }
        }

        else -> ExceptionMessage.UNKNOWN.message
    }

    enum class ExceptionMessage(val message :String) {
        SOCKET_TIMEOUT("Время ожидания соединения истекло."),
        IO("Ошибка ввода-вывода."),
        HTTP_400("Ошибка синхронизации."),
        HTTP_401("Ошибка авторизации."),
        HTTP_404("Элемент не найден."),
        HTTP_500("Ошибка сервера."),
        UNKNOWN("Неизвестная ошибка.")
    }
