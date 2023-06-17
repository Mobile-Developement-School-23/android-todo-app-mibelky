package ru.mobiledevschool.todoapp.utility

import android.content.res.Resources
import android.icu.text.SimpleDateFormat
import android.util.TypedValue
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

fun Int.dp(res: Resources) = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        toFloat(), res.displayMetrics
    ).roundToInt()

fun Long.toDateFormat() = SimpleDateFormat("dd MMMM yyyy", Locale("ru")).format(Date(this))
fun Date.toSimpleString() = SimpleDateFormat("dd MMMM yyyy", Locale("ru")).format(this)

