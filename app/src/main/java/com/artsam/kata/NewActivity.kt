package com.artsam.kata

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class NewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new)

        val btnBack = findViewById<AppCompatButton>(R.id.btnBack1)
        btnBack.setOnClickListener { finish() }
    }
}