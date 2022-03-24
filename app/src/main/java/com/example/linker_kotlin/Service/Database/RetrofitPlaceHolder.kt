package com.example.linker_kotlin.Service.Database

import com.example.linker_kotlin.Data.MyChatRoom
import com.example.linker_kotlin.Data.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RetrofitPlaceHolder {

    @GET("getUserbyID")
    fun getUserById(@Query("id") id : Int) : Call<User>

    @GET("getUsers")
    fun getAllUsers() : Call<List<User>>

    @GET("getChatRoomsByUserID")
    fun getChatRoomByUserId(@Query("userID") id : Int) : Call<List<MyChatRoom>>

    //@POST("addMessage")
    //fun addMessage(@Body message : Message) : Call<Int>

    //@GET("getCallHistoryByChatRoomId")
    //fun getCallHistoryByChatRoomId(@Query("id") id : Int) : Call<List<CallModel>>

    //@POST("addCall")
    //fun addCall(@Body call : CallModel) : Call<Int>

    @GET("getSingleChatRoomByUserIds")
    fun getSingleChatRoomByUserIds(@Query("id1") id1:String, @Query("id2") id2: String): Call<Int>

    @POST("addChatRoom")
    fun addChatRoom(@Body chatRoom : MyChatRoom) : Call<Int>

    //@POST("addUserInChat")
    //fun addUserInChat(@Body userInChatModel : UserInChatModel) : Call<Int>

    @GET("addSingleChatRoom")
    fun addSingleChatRoom() : Call<Int>

    //@POST("deleteUserInChat")
    //fun deleteUserInChat(@Body userInChatModel : UserInChatModel) : Call<Int>

    @GET("getStatusByUserId")
    fun getStatusByUserId(@Query("id") id :String) : Call<Int>

    @GET("setStatusByUserId")
    fun getStatusByUserId(@Query("id") id :String,
                          @Query("status")status : Int) : Call<Int>

    //@GET("getMessageById")
    //fun getMessageById(@Query("id") id : Int) : Call<Message>

    @GET("deleteMessageById")
    fun deleteMessageById(@Query("id") id :Int) : Call<Int>

    @GET("deleteChatRoomById")
    fun deleteChatRoomById(@Query("id") id : Int) :Call<Int>








}