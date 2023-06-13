package ru.mobiledevschool.todoapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ru.mobiledevschool.todoapp.databinding.ToDoItemBinding

class ToDoItemListAdapter : ListAdapter<ToDoItem, ToDoItemViewHolder>(DiffCallback) {

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
}