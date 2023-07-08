package ru.mobiledevschool.todoapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.mobiledevschool.todoapp.di.ActivityComponent

/**
 * Отвечает за отображение activity_main.xml, который в свою очередь содержит FragmentContainerView,
 * отвечающий за навигацию между MainFragment и NewItemFragment
 */
class MainActivity : AppCompatActivity() {

    lateinit var activityComponent: ActivityComponent
        private set
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        activityComponent = (application as ToDoApp).appComponent.activityComponent()
    }
}