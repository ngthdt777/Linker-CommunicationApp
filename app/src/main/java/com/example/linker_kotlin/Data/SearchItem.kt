package com.example.linker_kotlin.Data

class SearchItem : User() {
    var isClicked: Boolean = false
    private var id : String? = null
    private var display : String? = null
    private var picture: String? = null

    fun SearchItem(user : User ){
        this.id = user.getUserId()
        this.display = user.getDisplayName()
        this.picture = user.getProfilePicture()
        isClicked = false
    }

    fun click() {
        isClicked = !isClicked
    }
}
