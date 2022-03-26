package com.example.linker_kotlin.Service

import org.linphone.core.*

class ChatService {
    private lateinit var core: Core
    private val basicChatService: ChatService? = null
    private var remoteID_ChatroomIDMap: Map<String, ChatRoom>? = null

   /* fun initializeBasicChatService() {
        if (ChatService.basicChatService == null) {
            ChatService.basicChatService = ChatService()
        }
    }*/

    private fun ChatService() {
        core = LoginService.getInstance().getCore()
        remoteID_ChatroomIDMap = HashMap()
    }

    fun getChatroom(remoteURI: String?): ChatRoom? {
        var chatRoom = remoteID_ChatroomIDMap!![remoteURI!!]
        if (chatRoom == null) {
            chatRoom = createBasicChatRoom(remoteURI)
          //  remoteID_ChatroomIDMap.put(remoteURI, chatRoom)
        }
        return chatRoom
    }

    fun getService(): ChatService? {
        return basicChatService
    }


    fun createBasicChatRoom(remoteSipUri: String?): ChatRoom? {

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


       // chatMessage.addListener(chatMessageListener)
        //addMessageToHistory(chatMessage)

        // Send the message
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