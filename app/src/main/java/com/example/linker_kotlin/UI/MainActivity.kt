package com.example.linker_kotlin.UI

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.linker_kotlin.Data.CurrentUser
import com.example.linker_kotlin.LinkerApplication
import com.example.linker_kotlin.R
import com.example.linker_kotlin.Service.CallService
import com.example.linker_kotlin.Service.ChatService
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

public class MainActivity : FragmentActivity() {
    var title : TextView ?= null
    var callBtn : ImageButton ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_layout)

        title = findViewById(R.id.app_bar_name)
        callBtn = findViewById(R.id.appbar_call_button)
        callBtn?.isVisible = false

        val myProfilePic : CircleImageView = findViewById(R.id.app_bar_pic)
        Picasso.get().load(CurrentUser.getInstance().getUser()?.getProfilePicture()).into(myProfilePic)
        myProfilePic.setOnClickListener(){
            val intent = Intent(this,PersonalActivity::class.java)
            startActivity(intent)
        }

        ChatService.getInstance().ChatService()
        CallService.getInstance().CallService(this)

        val bottomNav : BottomNavigationView = findViewById(R.id.main_bottom_nav)
        bottomNav.setOnItemSelectedListener(navListener)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container,ChatFragment(),"CHAT").commit()

    }
    fun setAppBarTitle(newTitle:String) { title?.text = newTitle }

    fun updateChatRoom(chatRoomID : Int){
        val chat = 1
        val chatFragment = supportFragmentManager.findFragmentById(chat) as ChatFragment //tag CHAT
        if (chatFragment.isVisible){
            chatFragment.updateChatroom(chatRoomID)
        }
    }
    val navListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            var tag = "CHAT"
            when (item.itemId) {
                R.id.nav_chat -> selectedFragment = ChatFragment()
                R.id.nav_call -> {
                    tag = "CALL"
                    selectedFragment = CallFragment()
                }
            }
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container,selectedFragment!!, tag).commit()
            return@OnNavigationItemSelectedListener true
        }

}