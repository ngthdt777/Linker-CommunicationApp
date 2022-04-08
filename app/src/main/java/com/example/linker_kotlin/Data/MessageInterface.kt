package com.example.linker_kotlin.Data

import java.util.*

interface MessageInterface {
    fun getContent(): String?
    fun getSenderID(): String?
    fun getId(): Int
    fun getTimeSent(): Date?
    fun getMessageType(): Int
}