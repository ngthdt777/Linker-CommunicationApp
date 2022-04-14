package com.example.linker_kotlin.Model

import com.example.linker_kotlin.Data.MessageInterface
import com.google.gson.annotations.SerializedName
import java.util.*

class CallModel : MessageInterface {
    @SerializedName("id_call")
    private var callID: Int = 0

    @SerializedName("type")
    private var type = 0

    @SerializedName("fromuser")
    private var callerID: String? = null

    @SerializedName("time")
    private var time: Date? = null

    @SerializedName("id_cr")
    private var chatroomID = 0

    @SerializedName("duration")
    private var duration = 0

    fun CallModel(type: Int, callerID: String?, time: Date?, chatroomID: Int, duration: Int) {
        this.type = type
        this.callerID = callerID
        this.time = time
        this.chatroomID = chatroomID
        this.duration = duration
    }

    override fun getContent(): String {
        return "Call duration: " + Integer.toString(duration) + "s"
    }

    override fun getSenderID(): String? {
        return callerID
    }

    override fun getId(): Int {
        return callID
    }

    override fun getTimeSent(): Date? {
        return time
    }

    override fun getMessageType(): Int {
        return 1
    }

}

