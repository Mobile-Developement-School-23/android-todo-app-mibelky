package ru.mobiledevschool.todoapp.utility

import android.icu.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/*
* Глобальные extension функции-утилиты для преобразования единиц измерения
 */

fun Long.toDateFormat(): String = SimpleDateFormat("dd MMMM yyyy", Locale("ru")).format(Date(this))
fun Date.toSimpleString(): String = SimpleDateFormat("dd MMMM yyyy", Locale("ru")).format(this)

