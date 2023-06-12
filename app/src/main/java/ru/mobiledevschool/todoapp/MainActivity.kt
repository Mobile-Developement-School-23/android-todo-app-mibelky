package ru.mobiledevschool.todoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.mobiledevschool.todoapp.repo.ToDoItemsRepository

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}