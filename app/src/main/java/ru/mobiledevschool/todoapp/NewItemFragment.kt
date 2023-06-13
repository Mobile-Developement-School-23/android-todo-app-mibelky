package ru.mobiledevschool.todoapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        binding.deadlineSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            when (isChecked) {
                true -> {
                    val datePicker = MaterialDatePicker.Builder.datePicker()
                        .build()
                    datePicker.show(requireActivity().supportFragmentManager, "deadline date")
                }

                else -> {}  //TO-DO
            }
        }

        /** Priority textViews block click handler */
        // TODO: Add menu programmatically
        binding.priorityHeader.setOnClickListener {

        }

        /** Delete button click handler*/
        binding.deleteButton.setOnClickListener {
            findNavController().navigateUp()
        }




    }
}