package com.example.linker_kotlin.Data

import com.google.gson.annotations.SerializedName
import java.io.ObjectInput
import java.io.Serializable
import java.util.*

class User : Serializable {
    @SerializedName("id_user")
    private var userId : String ?= null

    @SerializedName("name_user")
    private var displayName : String ?= null

    @SerializedName("avatar_user")
    private var profilePicture : String ?= null

    fun User(userId : String, displayName : String){
        this.userId = userId
        this.displayName = displayName
        this.profilePicture = "0"
    }

    fun User(userId : String, displayName : String, profilePicture : Int){
        this.userId = userId
        this.displayName = displayName
        this.profilePicture = profilePicture.toString()
    }

    fun User(userId : String, displayName : String, profilePicture : String){
        this.userId = userId
        this.displayName = displayName
        this.profilePicture = profilePicture
    }

    fun User(u : User){
        this.userId = u.userId
        this.displayName = u.displayName
        this.profilePicture = u.profilePicture
    }

    fun getUserId() : String? { return userId }

    fun getDisplayName() : String? { return displayName }

    fun getProfilePictureInt() : Int? { return profilePicture?.toInt() }

    fun getProfilePicture() : String? { return profilePicture }

    fun getFirstName() : String? {
        val splits = displayName?.split(" ", ignoreCase = true, limit = 2 )?.first()
        return splits
    }

    override fun hashCode(): Int {
        return Objects.hash(userId,displayName,profilePicture)
    }
}































