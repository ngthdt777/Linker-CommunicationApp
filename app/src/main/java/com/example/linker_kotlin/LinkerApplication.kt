package com.example.linker_kotlin

import android.app.Application
import android.content.Context
import com.example.linker_kotlin.Data.MyChatRoom
import com.example.linker_kotlin.Data.User
import org.linphone.core.ChatRoom

class LinkerApplication : Application() {
    private val currentChatRooms = ArrayList<MyChatRoom>()
    private var context : Context ?= null
    override fun onCreate() {
        super.onCreate()
        this.context = applicationContext
    }

    fun getChatRoom(id : Int) : MyChatRoom? {
        for (chatRoom in this.currentChatRooms){
            if (chatRoom.getId() == id){
                return chatRoom
            }
        }
        return null
    }

    fun getLinphoneChatRoom(id: Int): ChatRoom? {
        for (chatRoom in currentChatRooms) {
            if (chatRoom.getId() == id) return chatRoom.getLinphoneChatRoom()
        }
        return null
    }

    fun getSingleChatroomUserList(): List<User?> {
        val list: MutableList<User?> = java.util.ArrayList<User?>()
        for (chatRoom in currentChatRooms) {
            if (chatRoom.getType() == 0) {
                list.addAll(chatRoom.getMembers()!!)
            }
        }
        return list
    }

    fun checkIfChatRoomExist(id: Int): Boolean {
        for (chatRoom in currentChatRooms) {
            if (chatRoom.getId() == id) return true
        }
        return false
    }

    fun addChatRoom(chatRoom: MyChatRoom) {
        if (!checkIfChatRoomExist(chatRoom.getId()!!)) {
            currentChatRooms.add(chatRoom)
        }
    }

    fun clearChatRoom() {
        currentChatRooms.clear()
    }
}