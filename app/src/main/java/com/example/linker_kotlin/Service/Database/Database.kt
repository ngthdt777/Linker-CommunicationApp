package com.example.linker_kotlin.Service.Database

import android.content.Context
import com.example.linker_kotlin.Service.LoginService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.linphone.core.Factory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Database {
    private var retrofit : Retrofit = TODO()
    private var retrofirPlaceHolder : RetrofitPlaceHolder = TODO()
    private object Holder { val INSTANCE = Database() }
    companion object{
        @JvmStatic
        fun getInstance(): Database {
            return Holder.INSTANCE
        }
    }
    fun Database() {
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()
        retrofit = Retrofit.Builder().baseUrl("http://hidden-peak-51170.herokuapp.com/api/")
            .addConverterFactory(GsonConverterFactory.create(gson)).build()
        retrofirPlaceHolder = retrofit.create(RetrofitPlaceHolder::class.java)
    }

    //fun initializeDatabase() { Database() = Database() }

    private fun getRetrofitPlaceHolder() : RetrofitPlaceHolder{ return retrofirPlaceHolder }

    fun getDatabase() : Database { return getInstance() }

    fun getAPI() : RetrofitPlaceHolder { return getInstance().getRetrofitPlaceHolder() }

}