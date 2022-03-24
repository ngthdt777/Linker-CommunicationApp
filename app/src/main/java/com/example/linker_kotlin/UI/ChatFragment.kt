package com.example.linker_kotlin.UI

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import com.example.linker_kotlin.Data.ChatListAdapter
import com.example.linker_kotlin.Data.CurrentUser
import com.example.linker_kotlin.Data.MyChatRoom
import com.example.linker_kotlin.Data.chatRoomArrayList
import com.example.linker_kotlin.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChatFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View?
        val view2: View?
        try{
            val listChatRoom = ArrayList<MyChatRoom>()
            view = inflater.inflate(R.layout.fragment_chat,container, false)
            view2 = inflater.inflate(R.layout.chat_fragment_item,container,false)
            var circleImageView = view2.findViewById<CircleImageView>(R.id.chat_group_profile_pic)

            //var currentUser = Current
            var currentUser = CurrentUser.getInstance().getUser()
            var listView = view.findViewById<ListView>(R.id.chat_fragment_list_view)
            var listAdapter = ChatListAdapter()

        } catch (e : Exception) {
            Log.d("FRA", "onCreateView", e);
            throw e;
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}