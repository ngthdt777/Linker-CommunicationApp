package com.example.linker_kotlin.UI

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.linker_kotlin.R
import org.w3c.dom.Text

class LoginActivity : AppCompatActivity() {

    private var loginStatusText : TextView ?= null
    private lateinit var userNameTextView : TextView
    private lateinit var passwordTextView : TextView
    private lateinit var loginBtn : Button
    private lateinit var toRegisterView : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_layout)

        loginBtn = findViewById(R.id.login_layout_login_btn)
        loginStatusText = findViewById(R.id.login_status_text)
        toRegisterView = findViewById(R.id.to_register_text)
        userNameTextView = findViewById(R.id.login_username)
        passwordTextView = findViewById(R.id.login_password)

        //intialize Linphone Core

        loginBtn.setOnClickListener{
            loginBtn.isEnabled = true
            changeLoginStatusText()
            var username = userNameTextView.text.toString()
            var password = passwordTextView.text.toString()
            //login function
        }

        toRegisterView.setOnClickListener{
            val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun changeLoginStatusText(){
        loginStatusText?.setText("Login to IMS server")
        loginStatusText?.setTextColor(Color.BLUE)
        loginBtn.isEnabled = false
    }






}