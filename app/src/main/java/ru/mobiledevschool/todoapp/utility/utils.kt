package ru.mobiledevschool.todoapp.utility

import android.content.res.Resources
import android.util.TypedValue
import kotlin.math.roundToInt

fun Int.dp(res: Resources) = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        toFloat(), res.displayMetrics
    ).roundToInt()