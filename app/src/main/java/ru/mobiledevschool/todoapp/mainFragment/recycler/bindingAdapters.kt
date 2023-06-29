package ru.mobiledevschool.todoapp.mainFragment.recycler

import android.graphics.Paint
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat.getColor
import ru.mobiledevschool.todoapp.R
import ru.mobiledevschool.todoapp.repo.ToDoItem
import ru.mobiledevschool.todoapp.utility.toSimpleString
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

fun TextView.bindDeadLineDate(date: Date?) {
    if (date == null) visibility = TextView.GONE
    else {
        visibility = TextView.VISIBLE
        text = date.toSimpleString()
    }
}

fun TextView.bindItemText(itemText: String, completed: Boolean) {
    text = itemText
    val typedColorValue = TypedValue()
    if (completed) {
        paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        context.theme.resolveAttribute(R.attr.colorLabelTertiary, typedColorValue, true)
        setTextColor(typedColorValue.data)
    } else {
        paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        context.theme.resolveAttribute(
            com.google.android.material.R.attr.colorOnPrimary,
            typedColorValue,
            true
        )
        setTextColor(typedColorValue.data)
    }
}

fun ImageButton.bindShowDoneImage(showDone: Boolean) {
    if (showDone) {
        setImageResource(R.drawable.unshow_done)
    } else {
        setImageResource(R.drawable.show_done)
    }
}

fun TextView.bindPriorityText(priority: ToDoItem.Priority) {
    val typedColorValue = TypedValue()
    when (priority) {
        ToDoItem.Priority.LOW -> {
            text = this.context.resources.getString(R.string.priority_menu_low_text)
            context.theme.resolveAttribute(R.attr.colorLabelTertiary, typedColorValue, true)
            setTextColor(typedColorValue.data)
        }

        ToDoItem.Priority.NORMAL -> {
            text = this.context.resources.getString(R.string.priority_menu_medium_text)
            context.theme.resolveAttribute(R.attr.colorLabelTertiary, typedColorValue, true)
            setTextColor(typedColorValue.data)
        }

        ToDoItem.Priority.HIGH -> {
            text = this.context.resources.getString(R.string.priority_menu_high_text)
            context.theme.resolveAttribute(R.attr.colorPriorityHigh, typedColorValue, true)
            setTextColor(typedColorValue.data)
        }
    }
}

fun ImageView.enable(key: Boolean) {
    if (key) setImageResource(R.drawable.delete_red)
    else setImageResource(R.drawable.delete_grey)
}

fun TextView.enable(key: Boolean) {
    val typedColorValue = TypedValue()
    if (key) context.theme.resolveAttribute(R.attr.colorPriorityHigh, typedColorValue, true)
    else context.theme.resolveAttribute(R.attr.colorDisabled, typedColorValue, true)
    setTextColor(typedColorValue.data)
}

fun TextView.bindDate(date: String) {
    text = date
    visibility = View.VISIBLE
}