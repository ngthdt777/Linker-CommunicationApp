package com.example.linker_kotlin.Service

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import com.example.linker_kotlin.Data.User
import com.example.linker_kotlin.Service.Database.Database
import com.example.linker_kotlin.UI.CallGoing
import com.example.linker_kotlin.UI.CallIncoming
import com.example.linker_kotlin.UI.MainActivity
import org.linphone.core.*
import retrofit2.Callback
import retrofit2.Response

class CallService {
    private lateinit var core : Core
    private lateinit var currentContext : Context
    private object Holder {
        @SuppressLint("StaticFieldLeak")
        val INSTANCE = CallService()
    }
    fun getCore() : Core { return core }

    private var isVideoCall: Boolean = false
    companion object{
        @JvmStatic
        fun getInstance() : CallService {
            return Holder.INSTANCE
        }
    }

    fun CallService(context: Context) {
        isVideoCall = false
        this.setCurrentContext(context)
        core = LoginService.getInstance().getCore()
        core.addListener(coreListenerStub)
        core.enableMic(true)
        core.enableVideoCapture(true)
        core.enableVideoDisplay(true)
        core.videoActivationPolicy.automaticallyAccept = true
        core.videoActivationPolicy.automaticallyInitiate = true
    }
    fun outgoingCall(remoteSipUri: String?, videoCall: Boolean) {
        isVideoCall = videoCall

        val remoteAddress = Factory.instance().createAddress(remoteSipUri!!) ?: return

        val callParams = core.createCallParams(null) ?: return

        callParams.mediaEncryption = MediaEncryption.None

        core.inviteAddressWithParams(remoteAddress, callParams)

    }

    private val coreListenerStub = object : CoreListenerStub() {
        override fun onCallStateChanged(
            core: Core,
            call: Call,
            state: Call.State?,
            message: String
        ) {
            super.onCallStateChanged(core, call, state, message)
            when(state) {
                Call.State.IncomingReceived -> {
                    val intent = Intent(currentContext, CallIncoming::class.java)
                    val remoteAddress = call.remoteAddress
                    val id = "sip:${remoteAddress.username}@${remoteAddress.domain}"
                    intent.putExtra("id",id)
                    Database.getInstance().getAPI().getUserById(id).enqueue(object : Callback<User> {
                        override fun onResponse( call : retrofit2.Call<User>, response : Response<User>
                        ){
                            intent.putExtra("picURI", response.body()?.getProfilePicture())
                            currentContext.startActivity(intent)
                        }

                        override fun onFailure(call: retrofit2.Call<User>, t: Throwable) {
                            intent.putExtra("picURI", "")
                            currentContext.startActivity(intent)
                        }
                    })
                }
                Call.State.OutgoingInit -> {
                    val intent = Intent(currentContext, CallGoing::class.java)
                    val remoteAddress = call.remoteAddress
                    val id = "sip:${remoteAddress.username}@${remoteAddress.domain}"
                    intent.putExtra("id",id)
                    intent.putExtra("receive",false)

                    Database.getInstance().getAPI().getUserById(id).enqueue(object : Callback<User> {
                        override fun onResponse( call : retrofit2.Call<User>, response : Response<User>
                        ){
                            intent.putExtra("picURI", response.body()?.getProfilePicture())
                            currentContext.startActivity(intent)
                        }
                        override fun onFailure(call: retrofit2.Call<User>, t: Throwable) {
                            intent.putExtra("picURI", "")
                            currentContext.startActivity(intent)
                        }
                    })
                }
                Call.State.OutgoingProgress -> {}
                Call.State.OutgoingRinging -> {}
                Call.State.Connected -> {}
                Call.State.StreamsRunning -> {
                   (currentContext as CallGoing).startTimer()
                    if (isVideoCall) {
                        (currentContext as CallGoing).changeToVideoCall()
                        isVideoCall = false
                    }
                }
                Call.State.Paused -> {}
                Call.State.PausedByRemote -> {}
                Call.State.Updating -> {}
                Call.State.UpdatedByRemote -> {
                    core.currentCall?.acceptUpdate(core.currentCall?.remoteParams)
                    if (core.currentCall?.params!!.videoEnabled()){
                        (currentContext as CallGoing).changeLayoutToVideo()
                    }
                    else (currentContext as CallGoing).changeLayoutToCall()
                }
                Call.State.Error -> {}
                Call.State.End -> {}
                Call.State.Released -> {
                    val intent = Intent(currentContext, MainActivity::class.java)
                    currentContext.startActivity(intent)
                }
                else -> return
            }
        }
    }

    fun hangUp() {
        if (core.callsNb == 0) return
        val call: Call? = if (core.currentCall != null) { core.currentCall }
        else {
            val listOfCalls = core.calls
            listOfCalls[0]
        }
        if (call == null) return
        call.terminate()
    }

    fun toggleVideo() {
        if (core.callsNb == 0) return

        val call = (if (core.currentCall != null)
            core.currentCall
        else core.calls.clone()[0]) ?: return

        val callParams = core.createCallParams(call)
        callParams?.enableVideo(!call.currentParams.videoEnabled())
        call.enableCamera(true)
        call.update(callParams)
    }

    fun toggleCamera() {
        val currentDevice = core.videoDevice
        for (camera in core.videoDevicesList) {
            if (camera != currentDevice && camera != "StaticImage: Static picture") {
                core.videoDevice = camera
                break
            }
        }
    }

    private fun pauseOrResume() {
        if (core.callsNb == 0) return

        val call = (if (core.currentCall != null) core.currentCall else core.calls.clone()[0]) ?: return
        if (call.state != Call.State.Paused && call.state != Call.State.Pausing) {
            call.pause()
        } else if (call.state != Call.State.Resuming) {
            call.resume()
        }
    }
    fun toggleSpeaker() {
        var currentAudioDevice = core.currentCall?.outputAudioDevice
        var speakerEnabled = false
        if (core.currentCall != null) {
            currentAudioDevice = core.outputAudioDevice
        }
        if (currentAudioDevice != null) {
            if (currentAudioDevice.type == AudioDevice.Type.Speaker) {
                speakerEnabled = true
            }
        }

        for (audioDevice in core.audioDevices) {
            if (speakerEnabled && audioDevice.type == AudioDevice.Type.Earpiece) {
                if (core.currentCall != null) {
                    core.currentCall!!.outputAudioDevice = audioDevice
                }
            } else if (!speakerEnabled && audioDevice.type == AudioDevice.Type.Speaker) {
                if (core.currentCall != null) {
                    core.currentCall!!.outputAudioDevice = audioDevice
                }
                return
            }
        }
    }

    fun getCurrentContext(): Context { return currentContext }

    fun setCurrentContext(currentContext: Context?) { this.currentContext = currentContext!! }
}