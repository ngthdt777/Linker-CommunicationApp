package com.example.linker_kotlin.Model

import com.example.linker_kotlin.Data.MessageInterface
import com.google.gson.annotations.SerializedName
import java.util.*

class CallModel(
    @SerializedName("type")
    private var type: Int,
    @SerializedName("fromuser")
    private var callerID: String?
    , @SerializedName("time")
    private var time: Date?,
    @SerializedName("id_cr")
    private var chatroomID: Int,
    @SerializedName("duration")
    private var duration: Int
) : MessageInterface {

    @SerializedName("id_call")
    private var callID: Int = 0

    override fun getContent(): String {
        return "Call duration: " + duration.toString() + "s"
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

