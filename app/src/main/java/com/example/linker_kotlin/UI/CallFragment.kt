package com.example.linker_kotlin.UI

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.linker_kotlin.Data.CallListAdapter
import com.example.linker_kotlin.Data.CurrentUser
import com.example.linker_kotlin.Data.User
import com.example.linker_kotlin.R
import com.example.linker_kotlin.Service.Database.Database
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.internal.ContextUtils
import com.google.android.material.internal.ContextUtils.getActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CallFragment : Fragment() {

    lateinit var listAdapter: CallListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View
        try {
            view = inflater.inflate(R.layout.fragment_call, container, false)
            val userArrayList: ArrayList<User> = ArrayList<User>()
            listAdapter = CallListAdapter(activity, userArrayList)
            val listView = view.findViewById<ListView>(R.id.call_list_view)
            listView.adapter = listAdapter
            Database.getInstance().getAPI().getAllUsers().enqueue(object : Callback<List<User?>> {
                override fun onResponse(call: Call<List<User?>>, response: Response<List<User?>>) {
                    userArrayList.clear()
                    for (user in response.body()) {
                        if (!user.getUserId()
                                .equals(CurrentUser.getInstance().getUser().getUserId())
                        ) {
                            userArrayList.add(user)
                        }
                    }
                    listAdapter.notifyDataSetChanged()
                }

                override fun onFailure(call: Call<List<User?>>, t: Throwable) {}
            })
            val floatingActionButton: FloatingActionButton = view.findViewById(R.id.fab_add_friend)
            floatingActionButton.setOnClickListener {
                val i = Intent(getActivity(), AddFriendActivity::class.java)
                startActivity(i)
            }
        } catch (e: Exception) {
            Log.d("FRA", "onCreateView", e)
            throw e
        }
        return view
    }
    fun onResume() {
        (activity as MainActivity).setAppBarTitle("Call")
        super.onResume()
    }
}

