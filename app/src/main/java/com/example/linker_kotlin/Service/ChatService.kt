package com.example.linker_kotlin.Service

import android.content.Context
import android.util.Log
import android.view.View
import com.example.linker_kotlin.Data.User
import com.example.linker_kotlin.Service.Database.Database
import com.example.linker_kotlin.UI.MainActivity
import org.linphone.core.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatService {
    private lateinit var core : Core
    private val basicChatService: ChatService? = null
    private var remoteID_ChatroomIDMap: Map<String, ChatRoom>? = null

    private fun ChatService() {
        core = LoginService.getInstance().getCore()
        LoginService.getInstance().getCore().addListener(setOnMessageReceive)
        remoteID_ChatroomIDMap = HashMap()
    }

    fun getChatRoom(remoteURI: String?): ChatRoom? {
        var chatRoom = remoteID_ChatroomIDMap!![remoteURI!!]
        if (chatRoom == null) {
            chatRoom = createBasicChatRoom(remoteURI)
                //remoteID_ChatroomIDMap.put(remoteURI, chatRoom)
            }
        return chatRoom
    }

    fun getService() : ChatService? { return basicChatService }

    private val setOnMessageReceive = object : CoreListenerStub() {
        override fun onMessageReceived(core: Core, chatRoom: ChatRoom, message: ChatMessage) {
            super.onMessageReceived(core, chatRoom, message)
            val currentContext : Context = CallService.getInstance().getCurrentContext()
            if (currentContext is ChatActivity ){
                val chatActivity = currentContext as ChatActivity
                chatActivity.updateMessage()
            } else if (currentContext is MainActivity) run {
                var idString = message.getCustomHeader("MessageID")
                var actionString = message.getCustomHeader("action");

                if (actionString == "updateChatroomList") {
                    currentContext.updateChatroom(-1)
                }
                if (idString != null){
                    var id = idString.toInt()
                    Database.getInstance().getAPI().getMessageById(id).enqueue(object : Callback<User> {
                        override fun onResponse(call: Call<User>, response: Response<User>) {
                            val mgs = response.body()
                            if (mgs != null){
                               val chatRoomId = mgs.getChatRoomId()
                               var mainActivity = currentContext as MainActivity
                               mainActivity.updateChatRoom(chatRoomId)
                            }
                        }
                        override fun onFailure(call: Call<User>, t: Throwable) {
                            TODO("Not yet implemented")
                        }
                    })
                }
            }
        }
    }

    fun createBasicChatRoom(remoteSipUri: String?) : ChatRoom? {
        val params = core.createDefaultChatRoomParams()
        params.backend = ChatRoomBackend.Basic
        params.enableEncryption(false)
        params.enableGroup(false)
        if (params.isValid) {
            val remoteAddress = Factory.instance().createAddress(
                remoteSipUri!!
            )
            if (remoteAddress != null) {
                val localAddress =
                    core.defaultAccount!!.params.identityAddress
                return core.createChatRoom(params, localAddress, arrayOf(remoteAddress))
            }
        }
        return null
    }

    fun sendMessage(mgs: String, chatRoom: ChatRoom, id: Int, actionString: String?) {
        var mgs = mgs
        val preMgs = "#####" + Integer.toString(id) + "#####"
        mgs = preMgs + mgs
        if (actionString == "updateChatroomList") {
            mgs = "*#Refresh_chatroomList#*!!"
        }

        // We need to create a ChatMessage object using the ChatRoom
        val chatMessage = chatRoom.createMessageFromUtf8(mgs)
        if (id != 0) {
            chatMessage.addCustomHeader("MessageID", Integer.toString(id))
        }
        if (actionString != null) {
            chatMessage.addCustomHeader("action", actionString)
        }

        val chatMessageListener = object : ChatMessageListenerStub() {
            override fun onMsgStateChanged(message: ChatMessage, state: ChatMessage.State?) {
                super.onMsgStateChanged(message, state)
                val messageView = message.userData as View
                when (state) {
                    ChatMessage.State.InProgress->{
                        Log.d("@TEXT", "onMsgStateChanged: in progress")
                    }
                    ChatMessage.State.Delivered-> {
                        Log.d("@TEXT", "onMsgStateChanged: in delivered");
                    }
                    ChatMessage.State.DeliveredToUser->{
                        Log.d("@TEXT", "onMsgStateChanged: in delivered to user");
                    }
                    ChatMessage.State.Displayed->{}
                    ChatMessage.State.NotDelivered->{}
                    ChatMessage.State.FileTransferDone->{}
                }
            }
        }

        chatMessage.addListener(chatMessageListener)
        addMessageToHistory(chatMessage)

        chatMessage.send()
    }

    private fun addMessageToHistory(chatMessage: ChatMessage) {
        // To display a chat message, iterate over it's contents list
        for (content in chatMessage.contents) {
            if (content.isText) {
                // Content is of type plain/text
            } else if (content.isFile) {
                // Content represents a file we received and downloaded or a file we sent
                // Here we assume it's an image
//                    if (content.name?.endsWith(".jpeg") == true ||
//                            content.name?.endsWith(".jpg") == true ||
//                            content.name?.endsWith(".png") == true) {
//                        addImageMessageToHistory(chatMessage, content)
//                    }
            } else if (content.isFileTransfer) {
//                    // Content represents a received file we didn't download yet
//                    addDownloadButtonToHistory(chatMessage, content)
            }
        }
    }
}