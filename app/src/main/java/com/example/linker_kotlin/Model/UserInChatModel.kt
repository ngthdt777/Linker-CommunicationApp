package com.example.linker_kotlin.Model

import com.google.gson.annotations.SerializedName

class UserInChatModel(
    @SerializedName("id_cr")
    val chatroomID: Int,
    @SerializedName("id_user")
    val userID: String
)
