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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import ru.mobiledevschool.todoapp.R
import ru.mobiledevschool.todoapp.ToDoApp
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

    //TODO: сделать by lazy
    private lateinit var viewModel: MainViewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            MainViewModel.Factory(ToDoApp.getLiveInstance())
        )[MainViewModel::class.java]

        /**                              RecyclerView                              */

        viewModel.listToShow.observe(viewLifecycleOwner) {
            toDoItemListAdapter.submitList(it)
        }

        initUi()

        /**                              FAB behavior                              */

        binding.addNewItemFab.setOnClickListener {
            val bundle = bundleOf("id" to null)
            findNavController().navigate(R.id.action_mainFragment_to_newItemFragment, bundle)
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


        val toolBarHeight = binding.motionLayout.minimumHeight

        ViewCompat.setOnApplyWindowInsetsListener(binding.motionLayout) { _, insets ->
            // Resizing motion layout in a collapsed state with needed insets to not overlap system bars
            val insetHeight = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            //val insetHeight = insets.systemWindowInsetTop
            binding.motionLayout.minimumHeight = toolBarHeight + insetHeight

            // Update guidelines with givens insets
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
        viewModel.deleteItemById(toDoItemListAdapter.currentList[position].id)
    }

    override fun subscribeOnTask(position: Int) {
        viewModel.checkItemById(toDoItemListAdapter.currentList[position].id)
        toDoItemListAdapter.notifyItemChanged(position)
    }

    private fun adapterCheckHandler(todoItem: ToDoItem) {
        viewModel.checkItemById(todoItem.id)
    }

    private fun adapterInfoHandler(toDoItem: ToDoItem) {
        val bundle = bundleOf("id" to toDoItem.id)
        findNavController().navigate(R.id.action_mainFragment_to_newItemFragment, bundle)
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
                reverseLayout = true
                stackFromEnd = true
            }
        }


        val itemTouchHelper = ItemTouchHelper(toDoListTouchHelper)
        itemTouchHelper.attachToRecyclerView(binding.toDoRecyclerView)

        binding.toDoRecyclerView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                // Вызываем метод clearSwipeState() после отпускания свайпа
                toDoListTouchHelper.clearState()
            }
            false
        }

    }
}