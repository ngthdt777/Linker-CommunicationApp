package com.example.linker_kotlin.UI

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.linker_kotlin.R
import com.example.linker_kotlin.Service.CallService
import com.example.linker_kotlin.Util.Utility
import org.linphone.core.AudioDevice


class CallVideo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_video)

        //-SET UP CALL SERVICE CORE
        CallService.getInstance().setCurrentContext(this)

        //-VIDEO FROM REMOTE DEVICE
        try {
            CallService.getInstance().getCore().nativeVideoWindowId = findViewById(R.id.remote_video_surface)
        } catch (e: Exception) {
            Utility().printToast(this, "[ E R R O R ] $e")
        }
        //-PREVIEW CAMERA FROM LOCAL DEVICE
        CallService.getInstance().getCore().nativePreviewWindowId = findViewById(R.id.local_preview_video_surface)

        //-CHECK FOR CAMERA PERMISSION
        val pm = this.packageManager
        val hasRecordPerm = pm.checkPermission(Manifest.permission.CAMERA,this.packageName)
        if (hasRecordPerm != PackageManager.PERMISSION_GRANTED) {
            val arrayPermission = arrayOf(Manifest.permission.CAMERA)
            requestPermissions(arrayPermission, 0)
        }

        //-BUTTON EVENT
        val camera = findViewById<ImageButton>(R.id.camera)
        camera.setOnClickListener { CallService.getInstance().toggleCamera() }
        val mic = findViewById<ImageButton>(R.id.mute)
        mic.setOnClickListener {
            CallService.getInstance().getCore().enableMic(!CallService.getInstance().getCore().micEnabled())
            if (CallService.getInstance().getCore().micEnabled())
                mic.setImageResource(R.drawable.call_unmute)
            else mic.setImageResource(R.drawable.call_mute)
        }
        val speaker = findViewById<ImageButton>(R.id.speaker)
        speaker.setOnClickListener {
            CallService.getInstance().toggleSpeaker()
            if (CallService.getInstance().getCore().currentCall?.outputAudioDevice?.type === AudioDevice.Type.Speaker)
                speaker.setImageResource(R.drawable.call_speaker)
            else speaker.setImageResource(R.drawable.call_headset)
        }
        val video = findViewById<ImageButton>(R.id.video)
        video.setOnClickListener {
            CallService.getInstance().toggleVideo()
            if (CallService.getInstance().getCore().currentCall?.params?.videoEnabled() == true ) {
                video.setImageResource(R.drawable.call_video)
            } else {
                video.setImageResource(R.drawable.call_unvideo)
            }
        }
        val end = findViewById<ImageButton>(R.id.end)
        end.setOnClickListener { CallService.getInstance().hangUp() }
    }
}