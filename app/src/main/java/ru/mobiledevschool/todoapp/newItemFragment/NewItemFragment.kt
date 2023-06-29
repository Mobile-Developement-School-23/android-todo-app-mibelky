package ru.mobiledevschool.todoapp.newItemFragment

import android.os.Bundle
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import ru.mobiledevschool.todoapp.R
import ru.mobiledevschool.todoapp.ToDoApp
import ru.mobiledevschool.todoapp.databinding.FragmentNewItemBinding
import ru.mobiledevschool.todoapp.mainFragment.recycler.bindDate
import ru.mobiledevschool.todoapp.mainFragment.recycler.bindDeadLineDate
import ru.mobiledevschool.todoapp.mainFragment.recycler.bindPriorityText
import ru.mobiledevschool.todoapp.mainFragment.recycler.enable
import ru.mobiledevschool.todoapp.repo.ToDoItem
import ru.mobiledevschool.todoapp.utility.toDateFormat

class NewItemFragment : Fragment() {

    private lateinit var binding: FragmentNewItemBinding
    private lateinit var viewModel: NewItemViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewItemBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            NewItemViewModel.Factory(ToDoApp.getLiveInstance())
        )[NewItemViewModel::class.java]

        arguments?.getString("id")?.let {
            viewModel.getItemById(it)
        }

        viewModel.item.observe(viewLifecycleOwner) { item ->
            item?.let {
                binding.itemText.setText(it.text, TextView.BufferType.EDITABLE)
                viewModel.updatePriority(it.priority)
                binding.deadline.bindDeadLineDate(it.deadLine)
                viewModel.startHighlight()
            }

        }

        viewModel.priority.observe(viewLifecycleOwner) {
            binding.priority.bindPriorityText(it)
        }

        viewModel.enableDelete.observe(viewLifecycleOwner) {
            binding.deleteIcon.enable(it)
            binding.deleteText.enable(it)
        }

        viewModel.saveEvent.observe(viewLifecycleOwner) { save ->
            if (save) {
                if (viewModel.item.value == null) {
                    viewModel.addNewItem(binding.itemText.text.toString())
                } else {
                    viewModel.updateById(binding.itemText.text.toString())
                }
                viewModel.endSaveEvent()
            }
        }

        viewModel.date.observe(viewLifecycleOwner) {
            binding.deadline.bindDate(it.toDateFormat())
        }

        /**                                Top app bar handler                               */

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.save -> {
                    viewModel.startSaveEvent()
                    findNavController().navigateUp()
                    true
                }

                else -> false
            }
        }

        /**                            Delete button click handler                            */

        binding.deleteText.setOnClickListener {
            viewModel.deleteItemById()
            findNavController().navigateUp()
        }
        binding.deleteIcon.setOnClickListener {
            viewModel.deleteItemById()
            findNavController().navigateUp()
        }

        /**                Deadline switch handler and date picker invocation                */

        binding.deadlineSwitch.setOnCheckedChangeListener { _, isChecked ->
            when (isChecked) {
                true -> {
                    val datePicker: MaterialDatePicker<Long> = MaterialDatePicker
                        .Builder
                        .datePicker()
                        .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                        .setTitleText("Дата выполнения")
                        .build()

                    datePicker.show(requireActivity().supportFragmentManager, "DATE_PICKER")

                    datePicker.addOnPositiveButtonClickListener {
                        viewModel.updateDeadLine(it)
                    }
                }

                else -> {}  //TODO: Add switch logic
            }
        }

        /**                Priority block click handler                */

        registerForContextMenu(binding.priorityBlock)

        binding.priorityBlock.setOnClickListener {
            it.showContextMenu(0.0F, 0.0F)
        }

        binding.priorityBlock.setOnLongClickListener { true }

    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = MenuInflater(requireContext())
        inflater.inflate(R.menu.priority_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.option_low -> {
                viewModel.updatePriority(ToDoItem.Priority.LOW)
                true
            }

            R.id.option_medium -> {
                viewModel.updatePriority(ToDoItem.Priority.NORMAL)
                true
            }

            R.id.option_high -> {
                viewModel.updatePriority(ToDoItem.Priority.HIGH)
                true
            }

            else -> super.onContextItemSelected(item)
        }
    }
}