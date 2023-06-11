package ru.mobiledevschool.todoapp

import android.widget.ImageView

fun ImageView.bindCompletionImage(completed: Boolean, priority: ToDoItem.Priority) {
    if (completed) {
        setImageResource(R.drawable.checked)
    } else if (priority == ToDoItem.Priority.HIGH) {
        setImageResource(R.drawable.unchecked_high)
    } else {
        setImageResource(R.drawable.unchecked)
    }
}