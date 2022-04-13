package com.example.linker_kotlin.Service.Database

import com.example.linker_kotlin.Data.Message
import com.example.linker_kotlin.Data.MyChatRoom
import com.example.linker_kotlin.Data.User
import com.example.linker_kotlin.Data.UserInChatModel
import com.example.linker_kotlin.Model.CallModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RetrofitPlaceHolder {

    @GET("getUserByID")
    fun getUserById(@Query("id") id : String) : Call<User>

    @GET("getUsers")
    fun getAllUsers() : Call<List<User>>

    @GET("getChatRoomsByUserID")
    fun getChatRoomByUserId(@Query("id") id: String?) : Call<List<MyChatRoom>>

    @POST("addMessage")
    fun addMessage(@Body message : Message) : Call<Int>

    @GET("getCallHistoryByChatroomID")
    fun getCallHistoryByChatRoomID(@Query("chatroomID") id : Int) : Call<List<CallModel>>

    @POST("addCall")
    fun addCall(@Body call : CallModel) : Call<Int>

    @GET("getMessagesByChatroomID")
    fun getMessagesByChatroomID(@Query("chatroomID") id: Int): Call<List<Message>>

    @GET("getSingleChatroomByUserIDs")
    fun getSingleChatRoomByUserIds(@Query("id1") id1:String, @Query("id2") id2: String): Call<Int>

    @POST("addChatRoom")
    fun addChatRoom(@Body chatRoom : MyChatRoom) : Call<Int>

    @POST("addUserInChat")
    fun addUserInChat(@Body userInChatModel : UserInChatModel) : Call<Int>

    @GET("addSingleChatroom")
    fun addSingleChatRoom() : Call<Int>

    @POST("deleteUserInChat")
    fun deleteUserInChat(@Body userInChatModel : UserInChatModel) : Call<Int>

    @GET("getStatusByUserID")
    fun getStatusByUserId(@Query("id") id :String) : Call<Int>

    @GET("setStatusByUserID")
    fun setStatusByUserId(@Query("id") id :String,
                          @Query("status")status : Int) : Call<Int>

    @GET("getMessagesByID")
    fun getMessagesById(@Query("id") id : Int) : Call<Message>

    @GET("deleteMessagesByID")
    fun deleteMessageById(@Query("id") id :Int) : Call<Int>


    @GET("deleteChatroomByID")
    fun deleteChatRoomById(@Query("id") id : Int) :Call<Int>








}