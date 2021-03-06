package com.example.linker_kotlin.Data

import android.content.Context
import com.example.linker_kotlin.Service.LoginService
import org.linphone.core.Factory

class CurrentUser {
    var user : User ?= null
    var status : Int ?= null

    private object Holder { val INSTANCE = CurrentUser() }
    companion object{
        @JvmStatic
        fun getInstance(): CurrentUser {
            return Holder.INSTANCE
        }
    }

    @JvmName("getUser1")
    fun getUser() : User? { return user}

    @JvmName("setUser1")
    fun setUser(user: User){ this.user = user}

    fun getStatus() : Int { return status!! }

    fun setStatus(status : Int) { this.status = status}

}