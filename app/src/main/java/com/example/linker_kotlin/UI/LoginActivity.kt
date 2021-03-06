package com.example.linker_kotlin.UI

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.linker_kotlin.R
import com.example.linker_kotlin.Service.Database.Database
import com.example.linker_kotlin.Service.LoginService
import java.sql.Date

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

        LoginService.getInstance().LoginService(this)
        LoginService.getInstance().setOnAccountRegisterStateChanged(this)

        loginBtn.setOnClickListener{
            loginBtn.isEnabled = true
            changeLoginStatusText("Login to IMS server", Color.BLUE, false)
            val username = userNameTextView.text.toString()
            val password = passwordTextView.text.toString()
            LoginService.getInstance().login(username,password)
        }

        toRegisterView.setOnClickListener{
            val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)
        }

        val pm = this.packageManager
        val hasRecordPerm = pm.checkPermission(
            Manifest.permission.RECORD_AUDIO,
            this.packageName
        )
        if (hasRecordPerm != PackageManager.PERMISSION_GRANTED) {
            val arrayPermission = arrayOf(Manifest.permission.RECORD_AUDIO)
            requestPermissions(arrayPermission, 0)
        }
    }

    fun changeLoginStatusText(text: String, color: Int, enableLoginBtn : Boolean){
        loginStatusText?.text = text
        loginStatusText?.setTextColor(color)
        loginBtn.isEnabled = enableLoginBtn
    }






}