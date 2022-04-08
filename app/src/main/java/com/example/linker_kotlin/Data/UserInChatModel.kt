package com.example.linker_kotlin.Data

import com.google.gson.annotations.SerializedName


class UserInChatModel(
    @SerializedName("ID_CR")
    val chatroomID: Int,
    @SerializedName("ID_User")
    val userID: String
)
