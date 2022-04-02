package com.example.linker_kotlin.Data

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.example.linker_kotlin.R
import com.example.linker_kotlin.Service.Database.Database
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/*class ChatListAdapter : ArrayAdapter<MyChatRoom>(mContext as Context,R.layout.chat_fragment_item,
                                                chatRoomArrayList as MutableList<MyChatRoom>){
    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val chatRoom = getItem(position)

        var v = convertView
        if ( v == null){
            v = LayoutInflater.from(mContext.mContext).inflate(R.layout.chat_fragment_item,null)
        }

        if (chatRoom != null ){
            var prominentMember = chatRoom.getPromientMember()

            val profileImage = v?.findViewById<CircleImageView>(R.id.profile_image)
            val profileGroupImage = v?.findViewById<CircleImageView>(R.id.chat_group_profile_pic)
            val username = v?.findViewById<TextView>(R.id.contact_name)
            var status = v?.findViewById<AppCompatImageView>(R.id.chat_list_status)

//            Database.getAPI().getStatusByUserID(prominentMember!!.getUserId())
//                .enqueue(object : Callback<Int?> {
//                    override fun onResponse(call: Call<Int?>, response: Response<Int?>) {
//                        val statuses = response.body()!!
//                        if (statuses == 0) status!!.setColorFilter(Color.RED) else status!!.setColorFilter(
//                            Color.GREEN
//                        )
//                    }
//
//                    override fun onFailure(call: Call<Int?>, t: Throwable) {}
//                })
            if (chatRoom.getName() != null){
                username?.text = chatRoom.getName()
                profileGroupImage?.visibility = View.VISIBLE
                val members = chatRoom.getTwoMembers()
                Picasso.get().load(members.get(0).getProfilePicture()).into(profileImage)
                Picasso.get().load(members.get(1).getProfilePicture()).into((profileGroupImage))
            }
            else {
                profileGroupImage?.visibility = View.INVISIBLE
                Picasso.get().load(chatRoom.getPromientMember()?.getProfilePicture()).into(profileImage)
                username?.text = chatRoom.getPromientMember()?.getDisplayName()
            }

            if (chatRoom.getHighLight() == 1){
                username?.setTypeface(null, Typeface.BOLD)
            }
        }

        return v!!
    }
    fun getChatRoomPosition(pos : Int): MyChatRoom { return chatRoomArrayList.chatRoomArrayList?.get(pos)!! }
}


@SuppressLint("StaticFieldLeak")
object mContext {
    var mContext : Context ?= null
}
object chatRoomArrayList {
    var chatRoomArrayList : List<MyChatRoom>? = null
}*/

class ChatListAdapter(private val mContext: Context, var chatRoomArrayList: List<MyChatRoom>) :
    ArrayAdapter<MyChatRoom?>(mContext, R.layout.chat_fragment_item, chatRoomArrayList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val chatRoom = getItem(position)
        var v = convertView
        if (v == null) {
            v = LayoutInflater.from(mContext).inflate(R.layout.chat_fragment_item, null)
        }
        if (chatRoom != null) {
            val prominentMember: User? = chatRoom.getPromientMember()
            val profileImage: CircleImageView = v!!.findViewById(R.id.profile_image)
            val profileGroupImage: CircleImageView = v.findViewById(R.id.chat_group_profile_pic)
            val username = v.findViewById<TextView>(R.id.contact_name)
            val status: AppCompatImageView = v.findViewById(R.id.chat_list_status)
            Database.getInstance().getAPI().getStatusByUserId(prominentMember?.getUserId()!!)
                .enqueue(object : Callback<Int?> {
                    override fun onResponse(call: Call<Int?>, response: Response<Int?>) {
                        val statuses = response.body()!!
                        if (statuses == 0) status.setColorFilter(Color.RED) else status.setColorFilter(
                            Color.GREEN
                        )
                    }

                    override fun onFailure(call: Call<Int?>, t: Throwable) {}
                })
            if (chatRoom.getName() != null) {
                username.text = chatRoom.getName()
                profileGroupImage.visibility = View.VISIBLE
                val members = chatRoom.getTwoMembers()
                Picasso.get().load(members[0].getProfilePicture()).into(profileImage)
                Picasso.get().load(members[1].getProfilePicture()).into(profileGroupImage)
            } else {
                profileGroupImage.visibility = View.INVISIBLE
                Picasso.get().load(chatRoom.getPromientMember()?.getProfilePicture())
                    .into(profileImage)
                username.text = chatRoom.getPromientMember()?.getDisplayName()
            }
            if (chatRoom.getHighLight() == 1) {
                username.setTypeface(null, Typeface.BOLD)
            }
        }
        return v!!
    }

    fun getChatroomByPosition(pos: Int): MyChatRoom {
        return chatRoomArrayList[pos]
    }
}
