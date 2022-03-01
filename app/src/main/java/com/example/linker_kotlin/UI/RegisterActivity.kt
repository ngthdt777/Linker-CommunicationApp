package com.example.linker_kotlin.UI

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.linker_kotlin.R

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_layout)

        var registerBtn = findViewById<Button>(R.id.register_layout_register_button)
        var toLoginText = findViewById<TextView>(R.id.to_sign_in_text)

        registerBtn.setOnClickListener{
            //
        }

        toLoginText.setOnClickListener{
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }

    }
}