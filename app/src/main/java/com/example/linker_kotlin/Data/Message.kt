package com.example.linker_kotlin.Data

import com.google.gson.annotations.SerializedName
import java.util.*

class Message(content: String,senderID: String? ,chatroomID: Int? ,createAt: Date?) : MessageInterface{
    @SerializedName("id_ms")
    private var id = 0

    @SerializedName("content")
    private var content = content

    @SerializedName("id_user")
    private var senderID = senderID

    @SerializedName("id_cr")
    private var chatroomID = chatroomID

    @SerializedName("time")
    private var createAt = createAt

//    fun Message(){
//        this.content = content
//        this.senderID = senderID
//        this.chatroomID = chatroomID
//        this.createAt = createAt
//    }

    override fun getContent(): String { return content }

    fun getChatroomID(): Int? { return chatroomID }

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
