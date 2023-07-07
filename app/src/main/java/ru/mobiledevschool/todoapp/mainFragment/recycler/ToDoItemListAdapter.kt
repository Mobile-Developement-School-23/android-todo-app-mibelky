package ru.mobiledevschool.todoapp.mainFragment.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.mobiledevschool.todoapp.databinding.ToDoItemBinding
import ru.mobiledevschool.todoapp.repo.ToDoItem

/*
* Кастомный класс-наследник ListAdapter отвечающий за поведение списка дел в RecyclerView
 */
class ToDoItemListAdapter(
    private val checkBoxHandler: (ToDoItem) -> Unit,
    private val infoHandler: (ToDoItem) -> Unit,
) : ListAdapter<ToDoItem, ToDoItemListAdapter.ToDoItemViewHolder>(
    DiffCallback
) {

    companion object DiffCallback : DiffUtil.ItemCallback<ToDoItem>() {
        override fun areItemsTheSame(oldItem: ToDoItem, newItem: ToDoItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: ToDoItem, newItem: ToDoItem): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ToDoItemBinding.inflate(layoutInflater, parent, false)
        return ToDoItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ToDoItemViewHolder, position: Int) {
        val toDoItem = getItem(position)
        holder.binding.checkBox.setOnClickListener {
            checkBoxHandler(toDoItem)
            notifyItemChanged(position)
        }
        holder.binding.priorityTextBlock.setOnClickListener {
            infoHandler(toDoItem)
        }
        holder.binding.infoIcon.setOnClickListener {
            infoHandler(toDoItem)
        }
        holder.onBind(toDoItem)
    }

    class ToDoItemViewHolder(val binding: ToDoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(toDoItem: ToDoItem) {
            binding.apply {
                itemText.bindItemText(toDoItem.text, toDoItem.completed)
                checkBox.bindCompletionImage(toDoItem.completed, toDoItem.priority)
                date.bindDeadLineDate(toDoItem.deadLine)
                priorityIcon.bindPriorityImage(toDoItem.completed, toDoItem.priority)
            }
        }
    }
}