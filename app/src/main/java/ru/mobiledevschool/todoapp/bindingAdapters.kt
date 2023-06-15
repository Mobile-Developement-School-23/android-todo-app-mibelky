package ru.mobiledevschool.todoapp

import android.widget.ImageButton
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

fun ImageButton.bindShowDoneImage(showDone: Boolean) {
    if (showDone) {
        setImageResource(R.drawable.unshow_done)
    } else {
        setImageResource(R.drawable.show_done)
    }
}