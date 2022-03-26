package com.example.linker_kotlin.Service

import android.content.Context
import org.linphone.core.Call
import org.linphone.core.Core
import org.linphone.core.Factory
import org.linphone.core.MediaEncryption

class CallService {
    private lateinit var core: Core
    private lateinit var currentContext:Context
    private object Holder { val INSTANCE = CallService() }
    private var isVideoCall: Boolean = false
    companion object{
        @JvmStatic
        fun getInstance() : CallService {
            return Holder.INSTANCE
        }
    }

    private fun CallService(context: Context) {
        isVideoCall = false
      //  core = LoginService.getLoginServer().getCore()
       // core.addListener(coreListenerStub)
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

    fun hangUp() {
        if (core.callsNb == 0) return

        val call: Call?
        call = if (core.currentCall != null) {
            core.currentCall
        } else {
            val listOfCalls = core.calls
            listOfCalls[0]
        }
        if (call == null) return

        call.terminate()
    }

    fun toggleVideo() {
        if (core.callsNb == 0) return

        val call = (if (core.currentCall != null) core.currentCall else core.calls.clone()[0])
            ?: return

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

    fun getCurrentContext(): Context? {
        return currentContext
    }

    fun setCurrentContext(currentContext: Context?) {
        this.currentContext = currentContext!!
    }
}