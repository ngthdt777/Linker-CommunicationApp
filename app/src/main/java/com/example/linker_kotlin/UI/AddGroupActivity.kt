package com.example.linker_kotlin.UI

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.linker_kotlin.Data.*
import com.example.linker_kotlin.Data.UserInChatModel
import com.example.linker_kotlin.R
import com.example.linker_kotlin.Service.Database.Database
import com.example.linker_kotlin.Util.Utility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddGroupActivity : AppCompatActivity() {
    private lateinit var contactRecyclerView: RecyclerView
    private lateinit var adapter: SearchContactAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var searchingContacts: ArrayList<SearchItem>
    private lateinit var groupList: MutableList<User>
    private lateinit var gridAdapter: AddPersonIconAdapter
    private lateinit var groupListGridView: RecyclerView
    private lateinit var addButton: Button
    private lateinit var groupName: EditText
    private lateinit var context: Context
    private lateinit var oldMemberList: MutableList<User>
    private var chatroomID = 0
    private var doneAddedDatabase = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_group_layout)

        setSupportActionBar(findViewById(R.id.search_my_toolbar))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        context = this
        oldMemberList = ArrayList()
        chatroomID = -1
        searchingContacts = ArrayList()
        groupList = ArrayList()
        addButton = findViewById(R.id.add_button_done)
        groupName = findViewById(R.id.group_name)

        val searchText = findViewById<EditText>(R.id.search_people_bar)
        searchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s)
            }
            override fun afterTextChanged(s: Editable) {}
        })

        // Set up adapter for search list
        contactRecyclerView = findViewById(R.id.add_group_recyclerView)
        layoutManager = LinearLayoutManager(this)
        adapter = SearchContactAdapter(this, searchingContacts)
        contactRecyclerView.layoutManager = layoutManager
        contactRecyclerView.adapter = adapter

        // Set up adapter for the group list
        val groupListLayoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false)
        groupListGridView = findViewById(R.id.group_recyclerView_view)
        groupListGridView.layoutManager = groupListLayoutManager
        gridAdapter = AddPersonIconAdapter(this, groupList as ArrayList<User>)
        groupListGridView.adapter = gridAdapter
        Database.getInstance().getAPI().getAllUsers().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                val extras = intent.extras
                if (extras != null) {
                    chatroomID = extras.getInt("id")
                    val chatroomName = extras.getString("chatroomName")
                    groupName.setText(chatroomName)
                    groupName.isEnabled = false
                    val list = intent.getSerializableExtra("MemberList")  as List<User>
                    for (user in list) {
                        if (Utility().notCurrentUser(user.getUserId()!!)) {
                            oldMemberList.add(user)
                        }
                    }
                }
                for (user in response.body()!!) {
                    if (user.getUserId() != CurrentUser.getInstance().getUser()?.getUserId()) {
                        val searchItem = SearchItem(user)
                        if (oldMemberList.contains(user)) {
                            searchItem.click()
                            groupList.add(user)
                        }
                        searchingContacts.add(searchItem)
                    }
                }
                adapter.notifyDataSetChanged()
                gridAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {}
        })
        addButton.setOnClickListener(View.OnClickListener {
            val chatroomName = groupName.text.toString()
            val membersList: MutableList<User> = groupList
            membersList.add(CurrentUser.getInstance().getUser()!!)
            if (groupList.size == 0) {
                Utility().printToast(applicationContext, "Group has to have a least 1 more member")
                return@OnClickListener
            }
            if (chatroomName.isEmpty()) {
                Utility().printToast(applicationContext, "Group has to have a name")
                return@OnClickListener
            }
            if (oldMemberList.isNotEmpty()) {
                val deletedMembers: List<User> = Utility().usersInAbutNotInB(oldMemberList, groupList)
                val addedMembers: List<User> = Utility().usersInAbutNotInB(groupList, oldMemberList)
                doneAddedDatabase = deletedMembers.size + addedMembers.size
                for (user in addedMembers) {
                    val model = UserInChatModel(chatroomID, user.getUserId()!!)
                    Database.getInstance().getAPI().addUserInChat(model).enqueue(object : Callback<Int?> {
                        override fun onResponse(call: Call<Int?>, response: Response<Int?>) {
                            doneAddedDatabase -= 1
                        }

                        override fun onFailure(call: Call<Int?>, t: Throwable) {}
                    })
                }
                for (user in deletedMembers) {
                    val model = UserInChatModel(chatroomID, user.getUserId()!!)
                    Database.getInstance().getAPI().deleteUserInChat(model).enqueue(object : Callback<Int?> {
                        override fun onResponse(call: Call<Int?>, response: Response<Int?>) {
                            doneAddedDatabase -= 1
                        }

                        override fun onFailure(call: Call<Int?>, t: Throwable) {}
                    })
                }
                Utility().printToast(applicationContext, "Modifying members")
                val waitTimer: CountDownTimer = object : CountDownTimer(700, 100) {
                    override fun onTick(millisUntilFinished: Long) {
                        if (doneAddedDatabase <= 0) {
                            Utility().sendUpdateMember(groupList)
                            val i = Intent(context, MainActivity::class.java)
                            startActivity(i)
                        }
                    }

                    override fun onFinish() {
                        Utility().sendUpdateMember(groupList)
                        val i = Intent(context, MainActivity::class.java)
                        startActivity(i)
                    }
                }.start()
                return@OnClickListener
            }
            val newChatRoom = MyChatRoom(chatroomName,1,membersList)
            Database.getInstance().getAPI().addChatRoom(newChatRoom).enqueue(object : Callback<Int?> {
                override fun onResponse(call: Call<Int?>, response: Response<Int?>) {
                    val idChatroom = response.body()!!
                    newChatRoom.setId(idChatroom)
                    for (user in membersList) {
                        val model = UserInChatModel(idChatroom, user.getUserId()!!)
                        Database.getInstance().getAPI().addUserInChat(model).enqueue(object : Callback<Int?> {
                            override fun onResponse(call: Call<Int?>, response: Response<Int?>) {
                                if (user.getUserId()
                                        .equals(membersList[membersList.size - 1].getUserId())
                                ) {
                                    Utility().sendUpdateMember(groupList)
                                    val i = Intent(context, MainActivity::class.java)
                                    startActivity(i)
                                }
                            }

                            override fun onFailure(call: Call<Int?>, t: Throwable) {}
                        })
                    }
                }

                override fun onFailure(call: Call<Int?>, t: Throwable) {}
            })
        })
    }

    fun addPerson(user: User) {
        if (groupList.contains(user)) {
            return
        }
        groupList.add(user)
        gridAdapter.notifyDataSetChanged()
    }

    fun removePerson(position: Int) {
        groupList.removeAt(position)
        gridAdapter.notifyDataSetChanged()
    }

    private fun setClickUserFromId(id: String?) {
        for (i in searchingContacts.indices) {
            val searchItem: SearchItem = searchingContacts[i]
            if (searchItem.getUserId().equals(id)) {
                searchItem.isClicked = false
                searchingContacts[i] = searchItem
                return
            }
        }
    }

    fun unCheckPerson(id: String?) {
        setClickUserFromId(id)
        adapter.notifyDataSetChanged()
    }
}