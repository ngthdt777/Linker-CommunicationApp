package com.example.linker_kotlin.UI

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.linker_kotlin.R
import com.example.linker_kotlin.Service.LoginService
import de.hdodenhof.circleimageview.CircleImageView

class PersonalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //CallService...
        setContentView(R.layout.activity_personnal)
        var context : Context = this

        setSupportActionBar(findViewById(R.id.profile_setting_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var profilePic :CircleImageView = findViewById(R.id.imageviewPr)
        var sipAddress : TextView = findViewById(R.id.emailPr)
        var name : TextView = findViewById(R.id.titlePr)

        //user.getinstance

        /*Picasso.get().load(user.getProfilePicture()).into(profilePic);
        sipAddress.setText(user.getUserId());
        name.setText(user.getDisplayName());*/

        val logout : TextView = findViewById(R.id.logout)
        logout.setOnClickListener {
            //Utility.printToast
            LoginService.getInstance().unregister()
        }
        val logoutImg : ImageView = findViewById(R.id.logoutImg)
        logoutImg.setOnClickListener {
            //Utility.printToast
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