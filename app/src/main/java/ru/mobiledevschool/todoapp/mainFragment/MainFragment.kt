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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.mobiledevschool.todoapp.R
import ru.mobiledevschool.todoapp.ToDoApp
import ru.mobiledevschool.todoapp.ToDoItemListAdapter
import ru.mobiledevschool.todoapp.bindShowDoneImage
import ru.mobiledevschool.todoapp.databinding.FragmentMainBinding
import ru.mobiledevschool.todoapp.dp
import kotlin.math.roundToInt

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeHelper: ItemTouchHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val displayMetrics = resources.displayMetrics
        val width = (displayMetrics.widthPixels / displayMetrics.density).toInt().dp(resources)
        val deleteColor =
            resources.getColor(R.color.color_light_red, requireActivity().theme)
        val checkColor =
            resources.getColor(R.color.color_light_green, requireActivity().theme)
        val deleteIcon = ResourcesCompat.getDrawable(resources, R.drawable.delete, null)
        val checkIcon = ResourcesCompat.getDrawable(resources, R.drawable.check, null)

        val repo = ToDoApp.getInstance()
        recyclerView = binding.toDoRecyclerView

        val toDoItemListAdapter = ToDoItemListAdapter()


        toDoItemListAdapter.submitList(repo.itemsToShow())
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        recyclerView.adapter = toDoItemListAdapter
        recyclerView.layoutManager = layoutManager

        /** FAB behavior */
        binding.addNewItemFab.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_newItemFragment)
        }

        /** Show done button behavior */
        binding.showDoneButton.bindShowDoneImage(repo.showDone)
        binding.showDoneButton.setOnClickListener {
            repo.changeDoneVisibility()
            (it as ImageButton).bindShowDoneImage(repo.showDone)
            //toDoItemListAdapter.notifyDataSetChanged()
        }

        /** AppBar behavior while scrolling */
        binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val seekPosition = -verticalOffset / appBarLayout.totalScrollRange.toFloat()
            binding.motionLayout.progress = seekPosition
        }


        // Desired collapsed height of toolbar
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

        /** Swipe behavior in RecyclerView*/

        swipeHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            //more code here
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                repo.deleteItemByPosition(position)
                toDoItemListAdapter.notifyItemRemoved(position)
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
                //1. Background color based upon direction swiped
                if (dX < 0) canvas.drawColor(deleteColor)
                if (dX > 0) canvas.drawColor(checkColor)

                //2. Printing the icons
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

                //3. Drawing icon based upon direction swiped
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