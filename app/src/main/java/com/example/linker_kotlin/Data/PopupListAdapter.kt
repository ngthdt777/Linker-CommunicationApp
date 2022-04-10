package com.example.linker_kotlin.Data

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.linker_kotlin.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class PopupListAdapter(var mContext: Context,chatRoomArrayList: List<User>)
    : ArrayAdapter<User>(mContext, R.layout.chat_fragment_item,chatRoomArrayList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val user = getItem(position)
        var v = convertView
        if (v == null)
            v = LayoutInflater.from(mContext).inflate(R.layout.chat_fragment_item,null)

        if (user != null){
            val profileImage = v?.findViewById<CircleImageView>(R.id.profile_image)
            val username = v?.findViewById<TextView>(R.id.contact_name)

            Picasso.get().load(user.getProfilePicture()).into(profileImage)
            username?.text = user.getDisplayName()
        }
        return v!!
    }
}