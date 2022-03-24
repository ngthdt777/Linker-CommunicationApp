package com.example.linker_kotlin.UI

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import org.jetbrains.annotations.NotNull
import de.hdodenhof.circleimageview.CircleImageView
import com.example.linker_kotlin.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import kotlinx.coroutines.selects.select
import org.linphone.core.ChatMessage

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
        val chatFragment = ChatFragment()
//        supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
//                                                   chatFragment,"CHAT").commit()
    }
    public fun setAppBarTitle(newTitle:String) { title?.text = newTitle }

    /*public fun void updateChatRoom(chatRoomID : Int){
        val chatFragment : ChatFragment = supportFragmentManager.findFragmentById("CHAT")
        if (chatFragment != null && chatFragment)
    }*/

    fun navListener() : NavigationBarView.OnItemReselectedListener? {
        fun setOnItemReselectedListener(@NonNull item: MenuItem) {
            var selectedFragment: Fragment? = null
            var tag: String = "CHAT"
            when (item.itemId) {
                R.id.nav_chat -> {
                    tag = "CHAT"
                    val chatFragment = ChatFragment()
                    selectedFragment = chatFragment
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