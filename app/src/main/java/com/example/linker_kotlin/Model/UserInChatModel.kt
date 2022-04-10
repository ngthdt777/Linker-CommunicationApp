package com.example.linker_kotlin.Model

import com.google.gson.annotations.SerializedName

class UserInChatModel(
    @SerializedName("ID_CR")
    val chatroomID: Int,
    @SerializedName("ID_User")
    val userID: String
)
