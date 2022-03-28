package com.example.linker_kotlin.Util

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import com.example.linker_kotlin.Data.CurrentUser
import com.example.linker_kotlin.Data.User
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class Utility {
    fun calculateNoOfColumns(
        context: Context,
        columnWidthDp: Float
    ): Int {
        val displayMetrics = context.resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        return (screenWidthDp / columnWidthDp + 0.5).toInt()
    }

    fun getScreenWidth(context: Context) : Float {
        val displayMetrics = context.resources.displayMetrics
        return displayMetrics.widthPixels / displayMetrics.density
    }

    fun getCurrentTime(): Date? {
        return Calendar.getInstance().time
    }

    fun printToast(context: Context?, mgs: String?) {
        Toast.makeText(context, mgs, Toast.LENGTH_LONG).show()
    }

    @SuppressLint("SimpleDateFormat")
    fun dateToStringFull(date: Date): String? {
        val pattern = "HH:mm"
        val df: DateFormat = SimpleDateFormat(pattern)
        df.timeZone = TimeZone.getTimeZone("GMT+14")
        return df.format(date)
    }

    fun notCurrentUser(userID: String): Boolean {
        return userID != CurrentUser.getInstance().getUser()?.getUserId()
    }

    /*fun sendUpdateMember(users: List<User?>) {
        for (user in users) {
            val room: ChatRoom = ChatService.getService().getChatroom(user.getUserId())
            ChatService.getService()
                .sendMessage("*#Refresh_chatroomList#*!!", room, 0, "updateChatroomList")
        }
    }*/

    fun usersInAbutNotInB(a: List<User>, b: List<User?>): List<User> {
        val result: MutableList<User> = ArrayList()
        for (user in a) {
            if (!b.contains(user)) {
                result.add(user)
            }
        }
        return result
    }
}