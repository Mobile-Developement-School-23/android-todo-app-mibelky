package ru.mobiledevschool.todoapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.mobiledevschool.todoapp.databinding.ToDoItemBinding

class ToDoItemListAdapter : ListAdapter<ToDoItem, ToDoItemListAdapter.ToDoItemViewHolder>(DiffCallback) {

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
        holder.itemView.setOnClickListener {
            it.findNavController().navigate(R.id.action_mainFragment_to_newItemFragment)
        }
        holder.onBind(toDoItem)
    }

    class ToDoItemViewHolder(private val binding: ToDoItemBinding) :
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