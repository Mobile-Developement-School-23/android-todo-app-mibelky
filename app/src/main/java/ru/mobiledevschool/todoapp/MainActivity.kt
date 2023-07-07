package ru.mobiledevschool.todoapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Отвечает за отображение activity_main.xml, который в свою очередь содержит FragmentContainerView,
 * отвечающий за навигацию между MainFragment и NewItemFragment
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}