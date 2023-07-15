package ru.mobiledevschool.todoapp.mainFragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.mobiledevschool.todoapp.MainActivity
import ru.mobiledevschool.todoapp.R
import ru.mobiledevschool.todoapp.databinding.FragmentMainBinding
import ru.mobiledevschool.todoapp.di.MainFragmentComponent
import ru.mobiledevschool.todoapp.mainFragment.recycler.ToDoItemListAdapter
import ru.mobiledevschool.todoapp.mainFragment.recycler.ToDoListTouchHelper
import ru.mobiledevschool.todoapp.mainFragment.recycler.bindShowDoneImage
import ru.mobiledevschool.todoapp.repo.ToDoItem

/**
 * Главный фрагмент, содержит список дел. Отвечает за представление View, нажатия, клики, свайпы.
 * Связан с ViewModel, откуда берет и куда передает данные.
 */
class MainFragment : Fragment(), ToDoListTouchHelper.SetupTaskBySwipe {

    private lateinit var component: MainFragmentComponent

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding ?: throw RuntimeException("Binding mustn't be null")

    private val toDoListTouchHelper by lazy {
        ToDoListTouchHelper(requireContext(), this)
    }

    private val toDoItemListAdapter by lazy {
        ToDoItemListAdapter(this::adapterCheckHandler, this::adapterInfoHandler)
    }

    private val viewModel: MainViewModel by viewModels {
        component.viewModelsFactory()
    }

//    private val requestPermissionLauncher =
//        registerForActivityResult(
//            ActivityResultContracts.RequestPermission()
//        ) { isGranted: Boolean ->
//            if (!isGranted) {
//
//                Snackbar.make(
//                    binding.root,
//                    "Нотификации помогут вам не пропустить запланированное.",
//                    Snackbar.LENGTH_INDEFINITE
//                )
//                    .setAction("Разрешить") { requestPermissionLauncher.launch(
//                        android.Manifest.permission.POST_NOTIFICATIONS
//                    ) }
//                    .show()}
//        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        when {
//            ContextCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.POST_NOTIFICATIONS
//            ) == PackageManager.PERMISSION_GRANTED -> {
//                // You can use the API that requires the permission.
//            }
//
//            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
//                Snackbar.make(
//                    binding.root,
//                    "Нотификации помогут вам не пропустить запланированное.",
//                    Snackbar.LENGTH_LONG
//                )
//                    .setAction("Разрешить") { requestPermissionLauncher.launch(
//                        Manifest.permission.POST_NOTIFICATIONS
//                    ) }
//                    .show()
//            }
//
//            else -> {
//            }
//        }

        component = (activity as MainActivity).activityComponent.mainFragmentComponent()
        component.inject(this)
        /**                              RecyclerView                              */

        viewModel.listToShow.observe(viewLifecycleOwner) {
            toDoItemListAdapter.submitList(it)
        }

        initUi()
        setMotionLayoutBehavior(binding.motionLayout)

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

    private fun setMotionLayoutBehavior(motionLayout: MotionLayout) {
        ViewCompat.setOnApplyWindowInsetsListener(motionLayout) { _, insets ->
            val toolBarHeight = motionLayout.minimumHeight
            val insetHeight = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            motionLayout.minimumHeight = toolBarHeight + insetHeight
            val endConstraintSet = motionLayout.getConstraintSet(R.id.collapsed)
            endConstraintSet.setGuidelineEnd(R.id.insets_guideline, toolBarHeight)
            endConstraintSet.setGuidelineEnd(
                R.id.collapsed_top_guideline,
                toolBarHeight + insetHeight
            )
            val startConstraintSet = motionLayout.getConstraintSet(R.id.expanded)
            startConstraintSet.setGuidelineBegin(R.id.collapsed_top_guideline, insetHeight)
            insets
        }
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


    private fun notificationPermissionFlow() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

        }
    }
}
