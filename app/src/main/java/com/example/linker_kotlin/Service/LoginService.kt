package com.example.linker_kotlin.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import com.example.linker_kotlin.Data.CurrentUser
import com.example.linker_kotlin.Data.User
import com.example.linker_kotlin.Service.Database.Database
import com.example.linker_kotlin.UI.LoginActivity
import com.example.linker_kotlin.UI.MainActivity
import org.linphone.core.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginService private constructor(){
    private lateinit var core : Core
    private lateinit var mUserName : String
    private lateinit var mDomain : String
    private lateinit var uID : String
    fun getCore() : Core { return core }
    private object Holder { val INSTANCE = LoginService() }
    companion object{
        @JvmStatic
        fun getInstance(): LoginService{
            return Holder.INSTANCE
        }
    }
    fun LoginService(context : Context) {
        Database.getInstance().Database()
        val factory = Factory.instance()
        factory.setDebugMode(true,"Hello Linphone")
        core = factory.createCore(null,null,context)
        core.start()
    }

    fun login (username: String, password : String){
        val domain = "sip.linphone.org"
        mUserName = username
        mDomain = domain
        val transportType = TransportType.Tcp
        val authInfo = Factory.instance().createAuthInfo(username, null, password,
                                                        null, null, domain, null)
        val accountParams : AccountParams = core.createAccountParams()
        val sipAddress = "sip:$username@$domain"
        val identity = Factory.instance().createAddress(sipAddress)
        accountParams.identityAddress = identity

        val address = Factory.instance().createAddress("sip:$domain")
        address?.transport = transportType

        accountParams.serverAddress = address
        accountParams.registerEnabled = true

        val account = core.createAccount(accountParams)

        //setOnAccountRegisterStateChanged()
        core.addAuthInfo(authInfo)
        core.addAccount(account)
        core.defaultAccount = account
        account.addListener { _, state, message ->
            Log.i("$state,----- $message","null")
        }
    }

    fun unregister(){
        val account : Account? = core.defaultAccount
        account ?: return
        val params = account.params
        val clonedParams = params.clone()
        clonedParams.registerEnabled = false
        account.params = clonedParams
    }

    fun delete(){
        val account = core.defaultAccount
        account ?: return
        core.removeAccount(account)
        core.clearAccounts()
        core.clearAllAuthInfo()
    }

    fun setOnAccountRegisterStateChanged(context : LoginActivity){
         val coreListener = object: CoreListenerStub(){
            override fun onAccountRegistrationStateChanged(core: Core, account: Account,
                                                        state: RegistrationState?, message: String)
            {
                super.onAccountRegistrationStateChanged(core, account, state, message)
                if (state == RegistrationState.None || state == RegistrationState.Cleared){
                    delete()
                    try {
                        //currentConext : Context = CallService.getService().getCurrentContext()
                        //current
                    } catch (e : Exception){
                        Log.d("@ON DER", "Cant change activity")
                    }
                }
                else if (state == RegistrationState.Failed){
                    CurrentUser.getInstance().setStatus(0)
                    context.changeLoginStatusText("Login Failed", Color.RED,true)
                }
                else if (state == RegistrationState.Ok){
                    CurrentUser.getInstance().setStatus(1)
                    val userID = "sip:$mUserName@$mDomain"
                    uID = userID
                    context.changeLoginStatusText("Fetching database",Color.BLUE,false)

//                    Database.getInstance().getAPI()
//                        .setStatusByUserId(userID, 1)
//                        .enqueue(object : Callback<Int> {
//                            override fun onResponse(call: Call<Int>, response: Response<Int>) {
//                                TODO("Not yet implemented")
//                            }
//
//                            override fun onFailure(call: Call<Int>, t: Throwable) {
//                                TODO("Not yet implemented")
//                            }
//                        })

                    Database.getInstance().getAPI().getUserById(userID).enqueue(object : Callback<User>{
                        override fun onResponse(call: Call<User>, response: Response<User>) {
                            if (!response.isSuccessful){
                                CurrentUser.getInstance().setStatus(0)
                            }

                            val user : User? = response.body()
                            CurrentUser.getInstance().setUser(user!!)
                            CurrentUser.getInstance().setStatus(1)
                            val intent = Intent(context.applicationContext, MainActivity::class.java)
                            context.startActivity(intent)
                        }
                        override fun onFailure(call: Call<User>, t: Throwable) {
                            CurrentUser.getInstance().setStatus(0)
                        }
                    })
                }
            }
        }
        core.addListener(coreListener)
    }

}