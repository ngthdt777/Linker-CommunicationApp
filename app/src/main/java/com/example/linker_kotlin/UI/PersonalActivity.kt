package com.example.linker_kotlin.UI

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.linker_kotlin.Data.CurrentUser
import com.example.linker_kotlin.Data.User
import com.example.linker_kotlin.R
import com.example.linker_kotlin.Service.CallService
import com.example.linker_kotlin.Service.LoginService
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class PersonalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CallService.getInstance().setCurrentContext(this)
        setContentView(R.layout.activity_personnal)
        var context : Context = this

        setSupportActionBar(findViewById(R.id.profile_setting_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val profilePic :CircleImageView = findViewById(R.id.imageviewPr)
        val sipAddress : TextView = findViewById(R.id.emailPr)
        val name : TextView = findViewById(R.id.titlePr)

        val user : User? = CurrentUser.getInstance().getUser()

        Picasso.get().load(user?.getProfilePicture()).into(profilePic)
        sipAddress.text = user?.getUserId()
        name.text = user?.getDisplayName()

        val logout : TextView = findViewById(R.id.logout)
        logout.setOnClickListener {
            LoginService.getInstance().unregister()
        }
        val logoutImg : ImageView = findViewById(R.id.logoutImg)
        logoutImg.setOnClickListener {
            LoginService.getInstance().unregister()
        }
        val contacts : TextView = findViewById(R.id.contacts)
        contacts.setOnClickListener {
            val intent = Intent(this,ContactListActivity::class.java)
            startActivity(intent)
        }
        val group = findViewById<TextView>(R.id.group)
        group.setOnClickListener {
            val intent = Intent(this,AddGroupActivity::class.java)
            startActivity(intent)
        }
        val accountSetting = findViewById<TextView>(R.id.account_setting)
        accountSetting.setOnClickListener {
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
    }
}