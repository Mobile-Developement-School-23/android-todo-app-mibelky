package ru.mobiledevschool.todoapp.mainFragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.mobiledevschool.todoapp.R
import ru.mobiledevschool.todoapp.databinding.FragmentMainBinding
import ru.mobiledevschool.todoapp.mainFragment.recycler.ToDoItemListAdapter
import ru.mobiledevschool.todoapp.mainFragment.recycler.ToDoListTouchHelper
import ru.mobiledevschool.todoapp.mainFragment.recycler.bindShowDoneImage
import ru.mobiledevschool.todoapp.repo.ToDoItem

class MainFragment : Fragment(), ToDoListTouchHelper.SetupTaskBySwipe {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding ?: throw RuntimeException("Binding mustn't be null")

    private val toDoListTouchHelper by lazy {
        ToDoListTouchHelper(requireContext(), this)
    }

    private val toDoItemListAdapter by lazy {
        ToDoItemListAdapter(this::adapterCheckHandler, this::adapterInfoHandler)
    }

    private val viewModel by viewModel<MainViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**                              RecyclerView                              */

        viewModel.listToShow.observe(viewLifecycleOwner) {
            toDoItemListAdapter.submitList(it)
        }

        initUi()

        /**                              FAB behavior                              */

        binding.addNewItemFab.setOnClickListener {
            viewModel.startNavigateEvent(null)
        }

        viewModel.navigateEvent.observe(viewLifecycleOwner) {
            if (it != "null") navigateTo(it)
        }


        /**                              Show done button behavior                              */

        viewModel.showDone.observe(viewLifecycleOwner) {
            binding.showDoneButton.bindShowDoneImage(it)
        }

        binding.showDoneButton.setOnClickListener {
            viewModel.changeVisibility()
        }

        /**                              AppBar behavior                                  */

        binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val seekPosition = -verticalOffset / appBarLayout.totalScrollRange.toFloat()
            binding.motionLayout.progress = seekPosition
        }

        viewModel.doneQuantity.observe(viewLifecycleOwner) {
            binding.doneText.text = getString(R.string.done_text_mock, it)
        }

        /**                              Snackbar error message                                  */
        viewModel.exceptionMessageEvent.observe(viewLifecycleOwner) {
            it?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT)
                    .show()
                viewModel.endHttpExceptionCodeEvent()
            }
        }



        ViewCompat.setOnApplyWindowInsetsListener(binding.motionLayout) { _, insets ->
            val toolBarHeight = binding.motionLayout.minimumHeight
            val insetHeight = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            binding.motionLayout.minimumHeight = toolBarHeight + insetHeight
            val endConstraintSet = binding.motionLayout.getConstraintSet(R.id.collapsed)
            endConstraintSet.setGuidelineEnd(R.id.insets_guideline, toolBarHeight)
            endConstraintSet.setGuidelineEnd(
                R.id.collapsed_top_guideline,
                toolBarHeight + insetHeight
            )
            val startConstraintSet = binding.motionLayout.getConstraintSet(R.id.expanded)
            startConstraintSet.setGuidelineBegin(R.id.collapsed_top_guideline, insetHeight)
            insets
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun deleteTask(position: Int) {
        viewModel.deleteItem(toDoItemListAdapter.currentList[position])
    }

    override fun subscribeOnTask(position: Int) {
        viewModel.checkItem(toDoItemListAdapter.currentList[position])
        toDoItemListAdapter.notifyItemChanged(position)
    }

    private fun adapterCheckHandler(todoItem: ToDoItem) {
        viewModel.checkItem(todoItem)
    }

    private fun adapterInfoHandler(toDoItem: ToDoItem) {
        viewModel.startNavigateEvent(toDoItem.id)
    }

    private fun navigateTo(id: String?) {
        viewModel.endNavigateEvent()
        findNavController().navigate(
            R.id.action_mainFragment_to_newItemFragment,
            bundleOf("id" to id)
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initUi() {
        binding.toDoRecyclerView.run {
            adapter = toDoItemListAdapter
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            ).apply {
                reverseLayout = false
                stackFromEnd = false
            }
        }


        val itemTouchHelper = ItemTouchHelper(toDoListTouchHelper)
        itemTouchHelper.attachToRecyclerView(binding.toDoRecyclerView)

        binding.toDoRecyclerView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                toDoListTouchHelper.clearState()
            }
            false
        }

    }
}