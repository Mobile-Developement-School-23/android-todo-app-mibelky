package ru.mobiledevschool.todoapp.mainFragment.recycler

import android.content.Context
import android.graphics.Canvas
import android.util.TypedValue
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.marginRight
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ru.mobiledevschool.todoapp.R
import ru.mobiledevschool.todoapp.utility.dp
import kotlin.math.roundToInt
import kotlin.properties.Delegates

class ToDoListTouchHelper(
    private val context: Context,
    private val setupTaskBySwipeImpl: SetupTaskBySwipe
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    val displayMetrics = context.resources.displayMetrics
    val width = (displayMetrics.widthPixels / displayMetrics.density).toInt().dp(context.resources)

    val deleteColor =
        context.resources.getColor(R.color.color_light_red, context.theme).toDrawable()
    val checkColor =
        context.resources.getColor(R.color.color_light_green, context.theme).toDrawable()

    val deleteIcon = ResourcesCompat.getDrawable(context.resources, R.drawable.delete, null)?: throwErrorIcon()
    val checkIcon = ResourcesCompat.getDrawable(context.resources, R.drawable.check, null)?: throwErrorIcon()

    var isSwipeInProgress: Boolean by Delegates.notNull()

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.bindingAdapterPosition

        when (direction) {
            ItemTouchHelper.LEFT -> setupTaskBySwipeImpl.deleteTask(position)
            ItemTouchHelper.RIGHT -> setupTaskBySwipeImpl.subscribeOnTask(position)
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

        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top
        val isSwipeRight = dX > 0

        isSwipeInProgress = actionState == ItemTouchHelper.ACTION_STATE_SWIPE && isCurrentlyActive

        if (isSwipeInProgress) {
            if (isSwipeRight) {
                checkColor.setBounds(
                    itemView.left,
                    itemView.top,
                    itemView.left + 72.dp,
                    itemView.bottom
                )
                checkColor.draw(canvas)
            } else {
                deleteColor.setBounds(
                    itemView.right - 72.dp,
                    itemView.top,
                    itemView.right + itemView.marginRight,
                    itemView.bottom
                )
                deleteColor.draw(canvas)
            }


            val iconTop = itemView.top + (itemHeight - deleteIcon.intrinsicHeight) / 2
            val iconBottom = iconTop + deleteIcon.intrinsicHeight
            val iconLeft: Int
            val iconRight: Int

            if (isSwipeRight) {
                iconLeft = itemView.left + 16.dp
                iconRight = itemView.left + 16.dp + checkIcon.intrinsicWidth
                checkIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                checkIcon.draw(canvas)
            } else {
                iconLeft = itemView.right - 16.dp - deleteIcon.intrinsicWidth
                iconRight = itemView.right - 16.dp
                deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                deleteIcon.draw(canvas)
            }
        }

        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }


    private fun throwErrorIcon(): Nothing = throw RuntimeException("resources not found")

    private val Int.dp
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            toFloat(), context.resources.displayMetrics
        ).roundToInt()

    interface SetupTaskBySwipe {
        fun deleteTask(position: Int)
        fun subscribeOnTask(position: Int)
    }

    fun clearState() {
        isSwipeInProgress = false
    }
}