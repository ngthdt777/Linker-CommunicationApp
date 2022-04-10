package com.example.linker_kotlin.Data

import com.google.gson.annotations.SerializedName
import java.util.*

class Message(content: String,senderID: String? ,chatroomID: Int? ,createAt: Date?) : MessageInterface{
    @SerializedName("ID_MS")
    private var id = 0

    @SerializedName("Content")
    private var content = content

    @SerializedName("ID_User")
    private var senderID = senderID

    @SerializedName("ID_CR")
    private var chatroomID = chatroomID

    @SerializedName("Time")
    private var createAt = createAt

//    fun Message(){
//        this.content = content
//        this.senderID = senderID
//        this.chatroomID = chatroomID
//        this.createAt = createAt
//    }

    override fun getContent(): String { return content }

    fun getChatroomID(): Int { return chatroomID }

    fun setChatroomID(chatroomID: Int) { this.chatroomID = chatroomID }

    fun getCreateAt(): Date? { return createAt }

    fun setCreateAt(createAt: Date?) { this.createAt = createAt }

    override fun getSenderID(): String? { return senderID }

    fun setSenderID(senderID: String?) { this.senderID = senderID }

    override fun getId(): Int { return id }

    override fun getMessageType(): Int { return 0 }

    override fun getTimeSent(): Date? { return createAt }

    fun setId(id: Int) { this.id = id }


}
