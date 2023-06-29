package ru.mobiledevschool.todoapp.mainFragment.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.mobiledevschool.todoapp.R
import ru.mobiledevschool.todoapp.repo.ToDoItem
import ru.mobiledevschool.todoapp.databinding.ToDoItemBinding

class ToDoItemListAdapter(val checkBoxClickListener: (id: Int) -> Unit) : ListAdapter<ToDoItem, ToDoItemListAdapter.ToDoItemViewHolder>(
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
        val id = getItemId(position).toInt()
        holder.binding.checkBox.setOnClickListener {
            checkBoxClickListener(id)
            notifyItemChanged(position)
        }
        holder.binding.priorityTextBlock.setOnClickListener {
            val bundle = bundleOf("id" to id.toString())
            it.findNavController().navigate(R.id.action_mainFragment_to_newItemFragment, bundle)
        }
        holder.binding.infoIcon.setOnClickListener {
            val bundle = bundleOf("id" to id.toString())
            it.findNavController().navigate(R.id.action_mainFragment_to_newItemFragment, bundle)
        }
        holder.onBind(toDoItem)
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).id.toLong()
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