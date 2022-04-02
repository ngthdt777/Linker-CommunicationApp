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
import com.example.linker_kotlin.R
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

        //ChatService.init...
        //CallService.init
        //ChatService.seton...

        val bottomNav : BottomNavigationView = findViewById(R.id.main_bottom_nav)
        bottomNav.setOnItemReselectedListener(navListener())
        //CallService.setCurrentContext...

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container,ChatFragment(),"CHAT").commit()
    }
    fun setAppBarTitle(newTitle:String) { title?.text = newTitle }

    fun updateChatRoom(chatRoomID : Int){
        val chat = 1
        val chatFragment = supportFragmentManager.findFragmentById(chat) //tag CHAT
        if (chatFragment != null && chatFragment.isVisible){
            //chatFragment.updateChatRoom(chatRoomID)
        }
    }

    fun navListener() : NavigationBarView.OnItemReselectedListener? {
        fun setOnItemReselectedListener(@NonNull item: MenuItem) {
            var selectedFragment: Fragment? = null
            var tag: String = "CHAT"
            when (item.itemId) {
                R.id.nav_chat -> {
                    tag = "CHAT"
                    selectedFragment = ChatFragment()
                }
                R.id.nav_call -> {
                    tag = "CALL"
//                    val callFragment = CallFragment()
//                    selectedFragment = callFragment
                }
            }
            if (selectedFragment != null) {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    selectedFragment,
                    tag
                ).commit()
            }
        }
        return null
    }
}