package ru.mobiledevschool.todoapp

import android.opengl.Visibility
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import java.time.LocalDate
import java.util.Date

fun ImageView.bindCompletionImage(completed: Boolean, priority: ToDoItem.Priority) {
    if (completed) {
        setImageResource(R.drawable.checked)
    } else if (priority == ToDoItem.Priority.HIGH) {
        setImageResource(R.drawable.unchecked_high)
    } else {
        setImageResource(R.drawable.unchecked)
    }
}

fun ImageView.bindPriorityImage(completed: Boolean, priority: ToDoItem.Priority) {
    when (priority) {
        ToDoItem.Priority.NORMAL -> visibility = ImageView.GONE
        ToDoItem.Priority.LOW -> {
            visibility = ImageView.VISIBLE
            setImageResource(R.drawable.priority_low)
        }
        ToDoItem.Priority.HIGH -> {
            visibility = ImageView.VISIBLE;
            if (!completed) {
                setImageResource(R.drawable.icon_priority_high)
            } else {
                setImageResource(R.drawable.icon_priority_high_completed)
            }
        }
    }
}

fun TextView.bindDeadLineDate(date: LocalDate?) {
    if (date == null) visibility = TextView.GONE
    else {
        visibility = TextView.VISIBLE
        text = date.toString()
    }
}

fun ImageButton.bindShowDoneImage(showDone: Boolean) {
    if (showDone) {
        setImageResource(R.drawable.unshow_done)
    } else {
        setImageResource(R.drawable.show_done)
    }
}