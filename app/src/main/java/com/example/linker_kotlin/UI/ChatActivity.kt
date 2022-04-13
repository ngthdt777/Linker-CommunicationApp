package com.example.linker_kotlin.UI

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.linker_kotlin.Data.*
import com.example.linker_kotlin.LinkerApplication
import com.example.linker_kotlin.Model.CallModel
import com.example.linker_kotlin.R
import com.example.linker_kotlin.Service.CallService
import com.example.linker_kotlin.Service.ChatService
import com.example.linker_kotlin.Service.Database.Database
import org.linphone.core.ChatRoom
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList


class ChatActivity : AppCompatActivity() {
    private lateinit var mMessageRecycler: RecyclerView
    private lateinit var messageListAdapter: MessageListAdapter
    private val CREATE_AT_EXAMPLE: Long = 400
    private lateinit var sendBtn: ImageView
    private lateinit var textBox: EditText
    private lateinit var messageList: MutableList<MessageInterface>
    private lateinit var callBnt: ImageButton
    private lateinit var memberInfoBtn: ImageButton
    private var myChatRoom: MyChatRoom? = null
    private lateinit var popupListAdapter: PopupListAdapter
    private lateinit var recyclerLayoutManager: LinearLayoutManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_layout)
        setSupportActionBar(findViewById(R.id.chat_my_toolbar))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        callBnt = findViewById(R.id.chat_call_button)
        memberInfoBtn = findViewById(R.id.chat_members_info)
        messageList = ArrayList()
        mMessageRecycler = findViewById(R.id.recyclerview_message_list)
        recyclerLayoutManager = LinearLayoutManager(this)
        mMessageRecycler.layoutManager = recyclerLayoutManager
        CallService.getInstance().setCurrentContext(this)
        val extras = intent.extras
        if (extras != null) {
            val id = extras.getInt("id")
            val displayName = extras.getString("displayName")
            myChatRoom = (this.application as LinkerApplication).getChatRoom(id)
            if (myChatRoom?.getLinphoneChatRoom() == null) {
                val newChatroom: ChatRoom? = ChatService.getInstance()
                    .createBasicChatRoom(myChatRoom?.getProminentMember()?.getUserId())
                if (newChatroom != null) {
                    myChatRoom?.setLinphoneChatRoom(newChatroom)
                } else {
                    Toast.makeText(this, "Cannot create chat room", Toast.LENGTH_LONG).show()
                }
            }
            messageListAdapter = MessageListAdapter(
                this,
                messageList, CurrentUser.getInstance().getUser()!!, myChatRoom!!
            )
            mMessageRecycler.adapter = messageListAdapter
            Database.getInstance().getAPI().getMessagesByChatroomID(myChatRoom?.getId()!!).enqueue(object : Callback<List<Message>> {
                    override fun onResponse(call: Call<List<Message>>,response: Response<List<Message>>) {
                        val messages = ArrayList<MessageInterface>(response.body() as List<MessageInterface>)
                        Database.getInstance().getAPI().getCallHistoryByChatRoomID(myChatRoom?.getId()!!).enqueue(object : Callback<List<CallModel>> {
                                override fun onResponse(call: Call<List<CallModel>>,response: Response<List<CallModel>>) {
                                    val calls: List<CallModel> = response.body()!!
                                    messages.addAll(calls)
                                    Collections.sort(messages,
                                        Comparator<MessageInterface>(){ o1, o2 ->
                                            o1.getTimeSent()!!.compareTo(o2.getTimeSent())
                                        })
                                    messageList.addAll(messages)
                                    messageListAdapter.notifyDataSetChanged()
                                    recyclerLayoutManager.scrollToPosition(messageListAdapter.itemCount - 1)
                                }

                                override fun onFailure(
                                    call: Call<List<CallModel>>,
                                    t: Throwable
                                ) {
                                }
                            })
                    }

                    override fun onFailure(call: Call<List<Message>>, t: Throwable) {}
                })
            callBnt.setOnClickListener(View.OnClickListener {
                CallService.getInstance().outgoingCall(myChatRoom?.getProminentMember()?.getUserId(), false)
            })
        } else {
            Toast.makeText(this, "Intent gone wrong", Toast.LENGTH_LONG).show()
        }
        sendBtn = findViewById(R.id.send_btn)
        textBox = findViewById(R.id.edittext_chatbox)
        sendBtn.setOnClickListener(View.OnClickListener {
            val mgs = textBox.text.toString()
            textBox.clearFocus()
            textBox.getText().clear()
            val curr_message = Message(mgs, CurrentUser.getInstance().getUser()?.getUserId(),
                                        myChatRoom?.getId(), Calendar.getInstance().time )
            Database.getInstance().getAPI().addMessage(curr_message).enqueue(object : Callback<Int?> {
                override fun onResponse(call: Call<Int?>, response: Response<Int?>) {
                    val id = response.body()!!
                    sendMessage(mgs, myChatRoom, id)
                    curr_message.setId(id)
                    //                        messageList.add(curr_message);
                    //                        messageListAdapter.notifyDataSetChanged();
                    updateMessage()
                }

                override fun onFailure(call: Call<Int?>, t: Throwable) {}
            })
        })
        memberInfoBtn.setOnClickListener(View.OnClickListener { onButtonShowPopupWindowClick(sendBtn) })
    }

    fun updateMessage() {
        Database.getInstance().getAPI().getMessagesByChatroomID(myChatRoom?.getId()!!)
            .enqueue(object : Callback<List<Message>> {
                override fun onResponse(call: Call<List<Message>>,response: Response<List<Message>>) {
                    val messages= ArrayList<MessageInterface>(response.body() as List<MessageInterface>)
                    Database.getInstance().getAPI().getCallHistoryByChatRoomID(myChatRoom?.getId()!!)
                        .enqueue(object : Callback<List<CallModel>> {
                            override fun onResponse(call: Call<List<CallModel>>,response: Response<List<CallModel>>) {
                                val calls: List<CallModel> = response.body()!!
                                messages.addAll(calls)
                                Collections.sort(messages,
                                    Comparator<MessageInterface>(){ o1, o2 ->
                                        o1.getTimeSent()!!.compareTo(o2.getTimeSent())
                                    })
                                messageList.addAll(messages)
                                messageListAdapter.notifyDataSetChanged()
                                recyclerLayoutManager.scrollToPosition(messageListAdapter.itemCount - 1)
                            }
                            override fun onFailure(call: Call<List<CallModel>?>, t: Throwable) {}
                        })
                }

                override fun onFailure(call: Call<List<Message>>, t: Throwable) {}
            })
    }

    private fun sendMessage(mgs: String, chatRoom: MyChatRoom?, id: Int) {
        if (chatRoom?.getType() == 1) {
            for (user in chatRoom.getMembers()!!) {
                if (!user?.getUserId().equals(CurrentUser.getInstance().getUser()?.getUserId())) {
                    val mChatRoom = ChatService.getInstance().getChatRoom(user?.getUserId()!!)
                    ChatService.getInstance().sendMessage(mgs, mChatRoom!!, id, null)
                }
            }
        } else {
            ChatService.getInstance().sendMessage(mgs, chatRoom?.getLinphoneChatRoom()!!, id, null)
        }
    }

    private fun onButtonShowPopupWindowClick(view: View?) {

        // inflate the layout of the popup window
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = inflater.inflate(R.layout.popup_group_members, null)

        // create the popup window
        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        var height = 600
        if (myChatRoom?.getMembers()?.size!! >= 3) {
            height = 800
        }
        val focusable = true // lets taps outside the popup also dismiss it
        val popupWindow = PopupWindow(popupView, 600, height, focusable)

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
        val memberList = popupView.findViewById<ListView>(R.id.popup_members_list)
        popupListAdapter = PopupListAdapter(this, (myChatRoom?.getMembers() as List<User>))
        memberList.adapter = popupListAdapter
        popupListAdapter.notifyDataSetChanged()
        val editMembersButton = popupView.findViewById<Button>(R.id.popup_edit_members_button)
        if (myChatRoom?.getType() == 0) {
            editMembersButton.visibility = View.INVISIBLE
        } else {
            editMembersButton.visibility = View.VISIBLE
        }
        editMembersButton.setOnClickListener {
            val i = Intent(applicationContext, AddGroupActivity::class.java)
            val memberList = myChatRoom?.getMembers()
            i.putExtra("id", myChatRoom?.getId())
            i.putExtra("chatroomName", myChatRoom?.getName())
            i.putExtra("MemberList", memberList as java.io.Serializable )
            startActivity(i)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.itemId == 10) {
            val messageInterface: MessageInterface =
                messageListAdapter.getMessageByPosition(item.groupId)
            if (messageInterface.getMessageType() != 1) {
                val id: Int = messageInterface.getId()
                Database.getInstance().getAPI().deleteMessageById(id).enqueue(object : Callback<Int?> {
                    override fun onResponse(call: Call<Int?>, response: Response<Int?>) {
                        Toast.makeText(applicationContext, "Message Deleted", Toast.LENGTH_LONG).show()
                        updateMessage()
                    }

                    override fun onFailure(call: Call<Int?>, t: Throwable) {}
                })
            }
        }
        return true
    }
}
