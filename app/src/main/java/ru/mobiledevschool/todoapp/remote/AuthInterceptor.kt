package ru.mobiledevschool.todoapp.remote

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
/*
* Класс-наследник Interceptor. Добавляет Header для авторизации в каждый запрос. В случае ошибок 404
* и 500 в ответе, повторяет такие запросы трижды.
 */
class AuthInterceptor : Interceptor {
    companion object {
        const val CODE_404 = 404
        const val CODE_500 = 500
        const val RETRY_TIMES = 3
        const val AUTH_HEADER_VALUE = "Bearer clagging"
    }
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val request = original.newBuilder()
            .header("Authorization", AUTH_HEADER_VALUE)
            .method(original.method(), original.body())
            .build()
        var response = chain.proceed(request)
        val codeToRetry = setOf(CODE_404, CODE_500)
        var tryCount = 0
        while (!response.isSuccessful && tryCount < RETRY_TIMES && response.code() in codeToRetry) {
            Log.d("intercept", "Request is not successful - $tryCount")
            tryCount++

            response.close()
            response = chain.proceed(request)
        }
        return response
    }
}