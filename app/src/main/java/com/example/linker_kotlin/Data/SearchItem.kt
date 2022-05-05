package com.example.linker_kotlin.Data

class SearchItem(user: User) : User() {
    var isClicked: Boolean = false

    init {
        this.User(user.getUserId()!!,
                    user.getDisplayName()!!,
                    user.getProfilePicture()!!)
        isClicked = false
    }

    fun click() {
        isClicked = !isClicked
    }
}
