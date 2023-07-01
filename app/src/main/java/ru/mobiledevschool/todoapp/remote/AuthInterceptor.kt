package ru.mobiledevschool.todoapp.remote

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request();

        val request = original.newBuilder()
            .header("Authorization", "Bearer clagging")
            .method(original.method(), original.body())
            .build()

        var response = chain.proceed(request)
        val codeToRetry = setOf<Int>(404, 500)
        var tryCount = 0

        while (!response.isSuccessful() && tryCount < 3 && response.code() in codeToRetry) {
            Log.d("intercept", "Request is not successful - " + tryCount);
            tryCount++;

            response.close()
            response = chain.proceed(request)
        }

        return response
    }
}