package com.example.linker_kotlin.UI

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.linker_kotlin.R
import com.example.linker_kotlin.Service.Database.Database

class OpeningActivity:AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.opening_layout)
        Database.getInstance().Database()
        val loginBtn = findViewById<Button>(R.id.login_button)
        val registerBtn = findViewById<Button>(R.id.register_button)

        if (!checkIfAlReadyHavePermission()){
            ActivityCompat.requestPermissions(this, arrayOf<String>(Manifest.permission.CAMERA),1)
        }

        loginBtn.setOnClickListener(){
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }

        registerBtn.setOnClickListener(){
            val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkIfAlReadyHavePermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)
        return result == PackageManager.PERMISSION_GRANTED
    }

    @Override
    public fun onRequestPermissionResult(requestCode: Int,
                                        @NonNull permission: CharArray,
                                        @NonNull grantResults: IntArray){
        when(requestCode){
            1->{
                if (grantResults.isNotEmpty() &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //
                }
                else {
                    Toast.makeText(this,"Permission denied to read your internet",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

