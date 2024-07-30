package com.artsam.kata.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.artsam.kata.R

class NewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new)

        val btnBack = findViewById<AppCompatButton>(R.id.btnBack1)
        btnBack.setOnClickListener { finish() }
    }
}