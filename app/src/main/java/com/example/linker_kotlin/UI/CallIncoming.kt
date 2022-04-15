package com.example.linker_kotlin.UI

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.linker_kotlin.R
import com.example.linker_kotlin.Service.CallService
import com.example.linker_kotlin.Util.Utility
import com.squareup.picasso.Picasso
import org.linphone.core.Call
import org.linphone.core.Reason

class CallIncoming : AppCompatActivity() {
    private lateinit var end: ImageButton
    private lateinit var accept: ImageButton
    private lateinit var mute: ImageButton
    private lateinit var speaker: ImageButton
    private lateinit var video: ImageButton
    private lateinit var callProfilePicture: ImageView
    private lateinit var callName: TextView
    private lateinit var avatarUrl: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_incoming)
        CallService.getInstance().setCurrentContext(this)
        callProfilePicture = findViewById(R.id.call_incoming_avatar)
        callName = findViewById(R.id.call_incoming_name)
        end = findViewById(R.id.end)
        end.setOnClickListener(View.OnClickListener {
            val currentCall: Call? =  CallService.getInstance().getCore().currentCall
            currentCall?.decline(Reason.Busy)?: Utility().printToast(applicationContext, "Current Call NULL")
        })

        val extras = intent.extras
        if (extras != null) {
            val id = extras.getString("id")
            val profilePicURI = extras.getString("picURI")
            avatarUrl = profilePicURI!!
            callName.text = id
            if (profilePicURI.isNotEmpty()) {
                Picasso.get().load(profilePicURI).into(callProfilePicture)
            }
        }
        accept = findViewById<View>(R.id.accept) as ImageButton
        accept.setOnClickListener {
            CallService.getInstance().getCore().currentCall?.accept()
            val i = Intent(this@CallIncoming, CallGoing::class.java)
            i.putExtra("id", callName.text)
            i.putExtra("picURI", avatarUrl)
            i.putExtra("receive", true)
            startActivity(i)
        }
    }
}