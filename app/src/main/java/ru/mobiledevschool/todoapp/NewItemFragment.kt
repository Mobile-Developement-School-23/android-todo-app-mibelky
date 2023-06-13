package ru.mobiledevschool.todoapp

import android.os.Bundle
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import ru.mobiledevschool.todoapp.R
import ru.mobiledevschool.todoapp.databinding.FragmentNewItemBinding

class NewItemFragment : Fragment() {

    private lateinit var binding: FragmentNewItemBinding

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

        /** Top app bar handler */
        binding.back.setOnClickListener {
            findNavController().navigateUp()
        }

        /** Deadline switch handler and date picker invocation */
        binding.deadlineSwitch.setOnCheckedChangeListener { _, isChecked ->
            when (isChecked) {
                true -> {
                    val datePicker = MaterialDatePicker.Builder.datePicker()
                        .build()
                    datePicker.show(requireActivity().supportFragmentManager, "deadline date")
                }

                else -> {}  //TO-DO
            }
        }

        /** Priority block click handler */
        // TODO: Add menu programmatically
        registerForContextMenu(binding.priorityBlock)

        binding.priorityBlock.setOnClickListener {
            it.showContextMenu(0.0F, 0.0F)
        }
        /** Handling longclick to prevent common context menu behavior */
        binding.priorityBlock.setOnLongClickListener { true }

        /** Delete button click handler*/
        binding.deleteButton.setOnClickListener {
            findNavController().navigateUp()
        }




    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = MenuInflater(requireContext())
        inflater.inflate(R.menu.priority_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        //val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        return when (item.itemId) {
            R.id.option_low -> {
                // Respond to context menu item 1 click.

                false
            }
            R.id.option_medium -> {
                // Respond to context menu item 2 click.
                false
            }
            else -> super.onContextItemSelected(item)
        }
    }
}