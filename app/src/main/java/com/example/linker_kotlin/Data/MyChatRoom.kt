package com.example.linker_kotlin.Data
import com.google.gson.annotations.SerializedName
import org.linphone.core.ChatRoom

class MyChatRoom {
    fun setId (id : Int) {this.id = id}

    @SerializedName("id_cr")
    private var id : Int ?= null

    @SerializedName("name_cr")
    private var name : String ?= null

    @SerializedName("type_cr")
    private var type : Int ?= null

    private var members : List<User> ?= null

    private var highlight : Int ?= null

    private var linphoneChatRoom : ChatRoom ?= null

    fun MyChatRoom(name : String, type : Int, members : List<User>){
        this.name = name
        this.type = type
        this.members = members
        highlight = 0
    }

    fun MyChatRoom(room : MyChatRoom){
        this.id = room.id
        this.name = room.name
        this.type = room.type
        highlight = 0
        linphoneChatRoom = null
        members = ArrayList()
        for (u : User? in room.getMembers()!!){
            if (u != null) {
                (members as ArrayList<User>).add(u)
            }
        }
    }

    fun getId(): Int? { return id }

    fun getName(): String? { return name }

    fun getType(): Int? { return type }

    fun getMembers(): List<User?>? { return members }

    fun getProminentMember() : User? {
        if (members?.size!! <= 1){
            return members?.get(0)
        }
        //Check if the other member is the current user or not
        if (members!![0].getUserId().equals(CurrentUser.getInstance().getUser()?.getUserId()) ){
            return members?.get(1)
        }
        return members?.get(0)
    }

    fun getLinphoneChatRoom() : ChatRoom? { return linphoneChatRoom}

    fun setLinphoneChatRoom(linphoneChatRoom: ChatRoom?) {
        this.linphoneChatRoom = linphoneChatRoom
    }

    fun getMemberByID(userID: String?): User? {
        for (user in members!!) {
            if (user.getUserId().equals(userID)) {
                return user
            }
        }
        return null
    }

    fun getTwoMembers() : List<User> {
        val twoMembers = ArrayList<User>()
        if (members?.size!! <= 2){
            return members!!
        }
        for (user : User in members!!){
            if (twoMembers.size >= 2){
                return twoMembers
            }
            if (!user.getUserId().equals(CurrentUser.getInstance().getUser()?.getUserId())) {
                twoMembers.add(user)
            }
        }
        return twoMembers
    }

    fun getHighLight() : Int? { return highlight }

    fun setHighLight( highLight : Int ) { this.highlight = highLight}
















}