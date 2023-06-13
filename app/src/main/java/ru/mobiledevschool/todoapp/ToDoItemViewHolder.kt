package ru.mobiledevschool.todoapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import ru.mobiledevschool.todoapp.databinding.ToDoItemBinding

class ToDoItemViewHolder(private val binding: ToDoItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun onBind(toDoItem: ToDoItem) {
        binding.itemText.text = toDoItem.text
        binding.checkBox.bindCompletionImage(toDoItem.completed, toDoItem.priority)
    }
}

class ToDoItemAdapter(private val toDoItems: ArrayList<ToDoItem>) :
    RecyclerView.Adapter<ToDoItemViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ToDoItemBinding.inflate(layoutInflater, parent, false)
        return ToDoItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ToDoItemViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            it.findNavController().navigate(R.id.action_mainFragment_to_newItemFragment)
        }
        holder.onBind(toDoItems[position])
    }

    override fun getItemCount(): Int {
        return toDoItems.size
    }
}