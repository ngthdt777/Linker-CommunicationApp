package com.example.linker_kotlin.Service.Database

import android.content.Context
import com.example.linker_kotlin.Service.LoginService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.linphone.core.Factory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Database {
    private lateinit var retrofit : Retrofit
    private lateinit var retrofitPlaceHolder : RetrofitPlaceHolder
    private object Holder { val INSTANCE = Database() }
    companion object{
        @JvmStatic
        fun getInstance() : Database {
            return Holder.INSTANCE
        }
    }
    fun Database() {
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()
        retrofit = Retrofit.Builder().baseUrl("https://rocky-temple-48054.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create(gson)).build()
        retrofitPlaceHolder = retrofit.create(RetrofitPlaceHolder::class.java)
    }

    private fun getRetrofitPlaceHolder() : RetrofitPlaceHolder{ return retrofitPlaceHolder }

    fun getDatabase() : Database { return getInstance() }

    fun getAPI() : RetrofitPlaceHolder { return getInstance().getRetrofitPlaceHolder() }

}