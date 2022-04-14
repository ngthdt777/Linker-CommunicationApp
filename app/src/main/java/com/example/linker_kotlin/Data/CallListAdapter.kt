package com.example.linker_kotlin.Data

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.example.linker_kotlin.R
import com.example.linker_kotlin.Service.CallService
import com.example.linker_kotlin.Service.Database.Database
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CallListAdapter(private val mContext: Context, userArrayList: ArrayList<User>) :
    ArrayAdapter<User>(mContext, R.layout.call_item, userArrayList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val user = getItem(position)
        var v = convertView
        if (v == null) {
            v = LayoutInflater.from(mContext).inflate(R.layout.call_item, null)
        }
        if (user != null) {
            val profileImage: CircleImageView = v!!.findViewById(R.id.profile_image)
            val username = v.findViewById<TextView>(R.id.contact_name)
            val callIcon: AppCompatImageView = v.findViewById(R.id.call_icon_call_item)
            val callVideoIcon: AppCompatImageView = v.findViewById(R.id.call_video_icon_call_item)
            val status: AppCompatImageView = v.findViewById(R.id.call_list_status)
            Database.getInstance().getAPI().getStatusByUserId(user.getUserId()!!).enqueue(object : Callback<Int?> {
                override fun onResponse(call: Call<Int?>, response: Response<Int?>) {
                    val statuses = response.body()!!
                    if (statuses == 0) status.setColorFilter(Color.RED) else status.setColorFilter(
                        Color.GREEN
                    )
                }

                override fun onFailure(call: Call<Int?>, t: Throwable) {}
            })
            callIcon.setOnClickListener {
                CallService.getInstance().outgoingCall(user.getUserId(), false)
            }
            callVideoIcon.setOnClickListener {
                CallService.getInstance().outgoingCall(user.getUserId(), true)
            }
            Picasso.get().load(user.getProfilePicture()).into(profileImage)
            username.text = user.getDisplayName()
        }
        return v!!
    }
}