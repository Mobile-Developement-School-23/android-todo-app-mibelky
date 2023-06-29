package ru.mobiledevschool.todoapp.mainFragment

import android.graphics.Canvas
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.mobiledevschool.todoapp.R
import ru.mobiledevschool.todoapp.ToDoApp
import ru.mobiledevschool.todoapp.mainFragment.recycler.ToDoItemListAdapter
import ru.mobiledevschool.todoapp.mainFragment.recycler.bindShowDoneImage
import ru.mobiledevschool.todoapp.databinding.FragmentMainBinding
import ru.mobiledevschool.todoapp.repo.ToDoItem
import ru.mobiledevschool.todoapp.utility.dp
import java.time.LocalDate
import kotlin.math.roundToInt

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeHelper: ItemTouchHelper
    private lateinit var viewModel: MainViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            MainViewModel.Factory(ToDoApp.getLiveInstance())
        )[MainViewModel::class.java]

        val displayMetrics = resources.displayMetrics
        val width = (displayMetrics.widthPixels / displayMetrics.density).toInt().dp(resources)
        val deleteColor =
            resources.getColor(R.color.color_light_red, requireActivity().theme)
        val checkColor =
            resources.getColor(R.color.color_light_green, requireActivity().theme)
        val deleteIcon = ResourcesCompat.getDrawable(resources, R.drawable.delete, null)
        val checkIcon = ResourcesCompat.getDrawable(resources, R.drawable.check, null)


        /**                              RecyclerView                              */

        val toDoItemListAdapter = ToDoItemListAdapter { id: Int -> viewModel.checkItemById(id) }
        //toDoItemListAdapter.submitList(viewModel.listToShow.value)
        viewModel.listToShow.observe(viewLifecycleOwner) {
            toDoItemListAdapter.submitList(it)
        }
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView = binding.toDoRecyclerView
        recyclerView.adapter = toDoItemListAdapter
        recyclerView.layoutManager = layoutManager

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

        /**                      Swipe behavior in RecyclerView                              */

        swipeHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val id = toDoItemListAdapter.getItemId(position).toInt()
                when (direction) {
                    ItemTouchHelper.LEFT -> viewModel.deleteItemById(id)
                    ItemTouchHelper.RIGHT -> {
                        viewModel.checkItemById(id)
                        toDoItemListAdapter.notifyItemChanged(position)
                    }
                }

            }

            override fun onChildDraw(
                canvas: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {

                if (dX < 0) canvas.drawColor(deleteColor)
                if (dX > 0) canvas.drawColor(checkColor)

                val textMargin = resources.getDimension(R.dimen.text_margin).roundToInt()

                checkIcon?.let {
                    it.bounds = Rect(
                        textMargin,
                        (viewHolder.itemView.top + viewHolder.itemView.bottom - it.intrinsicHeight) / 2,
                        textMargin + it.intrinsicWidth,
                        (viewHolder.itemView.top + viewHolder.itemView.bottom + it.intrinsicHeight) / 2
                    )
                }

                deleteIcon?.let {
                    it.bounds = Rect(
                        width - textMargin - it.intrinsicWidth - 10.dp(resources),
                        (viewHolder.itemView.top + viewHolder.itemView.bottom - it.intrinsicHeight) / 2,
                        width - textMargin - 10.dp(resources),
                        (viewHolder.itemView.top + viewHolder.itemView.bottom + it.intrinsicHeight) / 2
                    )
                }

                if (dX < 0) deleteIcon?.draw(canvas) else checkIcon?.draw(canvas)

                super.onChildDraw(
                    canvas,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        })

        swipeHelper.attachToRecyclerView(recyclerView)
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