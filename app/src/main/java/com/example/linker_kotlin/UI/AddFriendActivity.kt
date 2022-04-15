package com.example.linker_kotlin.UI

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.linker_kotlin.Data.PeopleContactAdapter
import com.example.linker_kotlin.Data.User
import com.example.linker_kotlin.LinkerApplication
import com.example.linker_kotlin.R
import com.example.linker_kotlin.Service.Database.Database
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddFriendActivity : AppCompatActivity() {
    private lateinit var contactRecyclerView: RecyclerView
    private lateinit var adapter: PeopleContactAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var searchingContacts: ArrayList<User>
    private lateinit var searchText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_people)
        setSupportActionBar(findViewById(R.id.add_friend_activity_toolbar))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        searchingContacts = ArrayList()
        searchText = findViewById(R.id.search_people_bar)
        searchText.isEnabled = false
        contactRecyclerView = findViewById(R.id.add_friend_recyclerView)
        layoutManager = LinearLayoutManager(this)
        adapter = PeopleContactAdapter(this, searchingContacts)
        adapter.clearData()
        contactRecyclerView.layoutManager = layoutManager
        contactRecyclerView.adapter = adapter
        Database.getInstance().getAPI().getAllUsers().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                val userList: List<User?> = LinkerApplication().getSingleChatroomUserList()
                for (user in response.body()!!) {
                    if (!userList.contains(user)) searchingContacts.add(user)
                }
                searchText.isEnabled = true
                adapter.notifyDataSetChanged()
            }
            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                searchText.isEnabled = true
            }
        })
        searchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s)
            }
            override fun afterTextChanged(s: Editable) {}
        })
    }

    fun removeContact(id: String?) {
        searchingContacts.removeIf { e: User ->
            e.getUserId().equals(id)
        }
        adapter.notifyDataSetChanged()
    }
}