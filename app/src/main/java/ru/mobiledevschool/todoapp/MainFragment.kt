package ru.mobiledevschool.todoapp

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.mobiledevschool.todoapp.databinding.FragmentMainBinding
import ru.mobiledevschool.todoapp.repo.ToDoItemsRepository

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private lateinit var recyclerView: RecyclerView
    val repo = ToDoItemsRepository()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.toDoRecyclerView
        val toDoAdapter = ToDoItemAdapter(repo.getItems())
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = toDoAdapter
        recyclerView.layoutManager = layoutManager

        binding.addNewItemFab.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_newItemFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater)
        return binding.root
    }
}