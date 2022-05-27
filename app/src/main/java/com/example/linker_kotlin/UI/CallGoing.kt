package com.example.linker_kotlin.UI

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.TextureView
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.linker_kotlin.Data.CurrentUser
import com.example.linker_kotlin.Model.CallModel
import com.example.linker_kotlin.R
import com.example.linker_kotlin.Service.CallService
import com.example.linker_kotlin.Service.Database.Database
import com.example.linker_kotlin.Util.Utility
import com.squareup.picasso.Picasso
import org.linphone.core.AudioDevice
import org.linphone.core.Call
import org.linphone.mediastream.video.capture.CaptureTextureView
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.math.roundToInt

class CallGoing : AppCompatActivity() {
    var speaker: ImageButton? = null
    var callProfilePicture: ImageView? = null
    var callName: TextView? = null
    var remoteVideo: TextureView? = null
    var previewLocalVideo: CaptureTextureView? = null
    var videoView: ConstraintLayout? = null
    var end: ImageButton? = null
    var buttonGroup: LinearLayout? = null
    private var video: ImageButton? = null
    private var startTime : Date? = null
    var timerText: TextView? = null
    var timer: Timer? = null
    var timerTask: TimerTask? = null
    var time = 0.0
    var timeStared = false
    var isVideoCall = false
    private var callerID : String? = null
    private var receiverID: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_going)
        CallService.getInstance().setCurrentContext(this)

        speaker = findViewById(R.id.call_going_speaker)
        callProfilePicture = findViewById(R.id.call_going_avatar)
        callName = findViewById(R.id.call_going_name)
        remoteVideo = findViewById(R.id.remote_video_surface)
        previewLocalVideo = findViewById(R.id.local_preview_video_surface)
        videoView = findViewById(R.id.video_view_layout)
        buttonGroup = findViewById(R.id.call_control_buttons)
        timerText = findViewById(R.id.timerText)
        isVideoCall = false

        CallService.getInstance().getCore().nativeVideoWindowId = remoteVideo
        CallService.getInstance().getCore().nativePreviewWindowId = previewLocalVideo

        timer = Timer()
        startTime = Utility().getCurrentTime()

        if (CallService.getInstance().getCore().currentCall?.state == Call.State.StreamsRunning){
            startTimer()
        }

        val pm = this.packageManager
        val hasRecordPerm = pm.checkPermission(
            Manifest.permission.CAMERA,
            this.packageName
        )
        if (hasRecordPerm != PackageManager.PERMISSION_GRANTED) {
            val arrayPermission = arrayOf(Manifest.permission.CAMERA)
            requestPermissions(arrayPermission, 0)
        }

        val extras = intent.extras
        if (extras != null) {
            val id = extras.getString("id")
            if (id != null) {
                receiverID = id
            }
            callerID = CurrentUser.getInstance().getUser()?.getUserId().toString()
            val profilePicURI = extras.getString("picURI")
            (callName as TextView).text = id
            val receive = extras.getBoolean("receive")
            if (receive) {
                if (id != null) {
                    callerID = id
                }
                receiverID = CurrentUser.getInstance().getUser()?.getUserId().toString()
            }
            if (profilePicURI!!.isNotEmpty()) {
                Picasso.get().load(profilePicURI).into(callProfilePicture)
            }
        }

        val mic = findViewById<ImageButton>(R.id.mute)
        mic.setOnClickListener {
            CallService.getInstance().getCore().enableMic(!CallService.getInstance().getCore().micEnabled())
            if (CallService.getInstance().getCore().micEnabled())
                mic.setImageResource(R.drawable.call_unmute)
            else mic.setImageResource(R.drawable.call_mute)
        }

        (speaker as ImageButton).setOnClickListener(View.OnClickListener {
            CallService.getInstance().toggleSpeaker()
            if (CallService.getInstance().getCore().currentCall?.outputAudioDevice?.type == AudioDevice.Type.Speaker)
                (speaker as ImageButton).setImageResource(R.drawable.call_speaker)
            else (speaker as ImageButton).setImageResource(R.drawable.call_headset)
        })

        video = findViewById(R.id.video)
        (video as ImageButton).setOnClickListener(View.OnClickListener {
            CallService.getInstance().toggleVideo()
            if (CallService.getInstance().getCore().currentCall?.params?.videoEnabled() == true) {
                changeLayoutToVideo()
                (video as ImageButton).setImageResource(R.drawable.call_video)
            } else {
                changeLayoutToCall()
                (video as ImageButton).setImageResource(R.drawable.call_unvideo)
            }
        })

        (previewLocalVideo as CaptureTextureView).setOnClickListener(View.OnClickListener {
            CallService.getInstance().toggleCamera()
        })

        end = findViewById(R.id.end)
        end!!.setOnClickListener{
            Database.getInstance().getAPI().getSingleChatRoomByUserIds(callerID!!, receiverID!!).enqueue(object : Callback<Int>{
                override fun onResponse(call: retrofit2.Call<Int>, response: Response<Int>) {
                    val duration: Int = time.roundToInt()
                    val callModel = CallModel(1,
                                            callerID!!,
                                            startTime!!,
                                            response.body() as Int,
                                            duration)
                    Database.getInstance().getAPI().addCall(callModel).enqueue(object : Callback<Int> {
                        override fun onResponse(call: retrofit2.Call<Int>,response: Response<Int>) {
                            CallService.getInstance().hangUp()
                        }
                        override fun onFailure(call: retrofit2.Call<Int>, t: Throwable) {}
                    })
                }

                override fun onFailure(call: retrofit2.Call<Int>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

    fun changeVideoIcon(){
        if (CallService.getInstance().getCore().currentCall?.params?.videoEnabled() == true)
            (video as ImageButton).setImageResource(R.drawable.call_video)
        else (video as ImageButton).setImageResource(R.drawable.call_unvideo)
    }
    fun changeLayoutToCall() {
        videoView?.visibility = View.INVISIBLE
        callName?.visibility = View.VISIBLE
        callProfilePicture?.visibility = View.VISIBLE
        val marginParams = end!!.layoutParams as MarginLayoutParams
        marginParams.setMargins(0, 0, 0, 150)
        val marginParamsControl = buttonGroup!!.layoutParams as MarginLayoutParams
        marginParamsControl.setMargins(0, 0, 0, 40)
    }

    fun changeLayoutToVideo() {
        videoView?.visibility = View.VISIBLE
        callName?.visibility = View.INVISIBLE
        callProfilePicture?.visibility = View.INVISIBLE
        val marginParams = end!!.layoutParams as MarginLayoutParams
        marginParams.setMargins(0, 0, 0, 5)
        val marginParamsControl = buttonGroup!!.layoutParams as MarginLayoutParams
        marginParamsControl.setMargins(0, 0, 0, 2)
    }

    fun startTimer() {
        timerText!!.visibility = View.VISIBLE
        if (!timeStared) {
            timeStared = true
            timerTask = object : TimerTask() {
                override fun run() {
                    runOnUiThread {
                        time++
                        timerText!!.text = getTimerText()
                    }
                }
            }
        }
        timer!!.scheduleAtFixedRate(timerTask, 0, 1000)
    }

    @SuppressLint("SetTextI18n")
    fun handUp() {
        timeStared = false
        timerTask!!.cancel()
        timerText!!.text = "Released"
    }

    private fun getTimerText(): String {
        val rounded = time.roundToInt()
        val seconds = rounded % 86400 % 3600 % 60
        val minutes = rounded % 86400 % 3600 / 60
        val hours = rounded % 86400 / 3600
        return formatTime(seconds, minutes, hours)
    }

    @SuppressLint("DefaultLocale")
    private fun formatTime(seconds: Int, minutes: Int, hours: Int): String {
        return String.format("%02d", hours) + " : " + String.format(
            "%02d",
            minutes
        ) + " : " + String.format("%02d", seconds)
    }

    fun changeToVideoCall() {
        video!!.performClick()
    }

}