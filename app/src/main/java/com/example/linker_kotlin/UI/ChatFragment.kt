package com.example.linker_kotlin.UI

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView.AdapterContextMenuInfo
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.linker_kotlin.Data.ChatListAdapter
import com.example.linker_kotlin.Data.CurrentUser
import com.example.linker_kotlin.Data.MyChatRoom
import com.example.linker_kotlin.LinkerApplication
import com.example.linker_kotlin.R
import com.example.linker_kotlin.Service.Database.Database
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatFragment : Fragment() {

    private lateinit var listAdapter: ChatListAdapter
    private lateinit var chatRoomList: MutableList<MyChatRoom>
    private lateinit var listView: ListView
    private lateinit var  circleImageView : CircleImageView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        lateinit var view : View
        lateinit var view2: View
        try{
            chatRoomList = ArrayList()
            view = inflater.inflate(R.layout.fragment_chat,container, false)
            view2 = inflater.inflate(R.layout.chat_fragment_item,container,false)
            circleImageView = view2.findViewById(R.id.chat_group_profile_pic)

            val currentUser = CurrentUser.getInstance().getUser()
            listView = view.findViewById(R.id.chat_fragment_list_view)
            listAdapter = ChatListAdapter(this.requireActivity() ,chatRoomList)
            listView.adapter = listAdapter

            registerForContextMenu(listView)

            Database.getInstance().getAPI().getChatRoomByUserId(currentUser?.getUserId()).enqueue(object : Callback<List<MyChatRoom>> {
                    override fun onResponse(call: Call<List<MyChatRoom>>,response: Response<List<MyChatRoom>>) {
                        (requireActivity().application as LinkerApplication).clearChatRoom()
                        val chatRooms : List<MyChatRoom>? = response.body()
                        if (chatRooms != null) {
                            for (chatRoom in chatRooms){
                                chatRoomList.add(chatRoom)
                                (requireActivity().application as LinkerApplication).addChatRoom(chatRoom)
                            }
                        }
                        listAdapter.notifyDataSetChanged()
                    }
                    override fun onFailure(call: Call<List<MyChatRoom>>, t: Throwable) {
                        TODO("Not yet implemented")
                    }
            })

            listView.isClickable = true
            listView.onItemClickListener =
                OnItemClickListener { parent, view, position, id ->
                    val clickedChatroom: MyChatRoom = chatRoomList[position]
                    val i = Intent(this.context, ChatActivity::class.java)
                    i.putExtra("id", clickedChatroom.getId())
                    i.putExtra("displayName", clickedChatroom.getProminentMember()?.getDisplayName())
                    startActivity(i)
                }

            val floatingActionButton = view.findViewById<FloatingActionButton>(R.id.fab)
            floatingActionButton.setOnClickListener {
                val i = Intent(activity, AddGroupActivity::class.java)
                startActivity(i)
            }
        }
        catch (e : Exception) {
            Log.d("FRA", "onCreateView", e);
            throw e;
        }
        return view
    }

    override fun onCreateContextMenu(menu: ContextMenu,v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val menuInflater = requireActivity().menuInflater
        menuInflater.inflate(R.menu.long_click_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.delete_option) {
            val info = item.menuInfo as AdapterContextMenuInfo
            val chatRoom: MyChatRoom = listAdapter.getChatroomByPosition(info.position)
            Database.getInstance().getAPI().deleteChatRoomById(chatRoom.getId()!!).enqueue(object : Callback<Int?> {
                override fun onResponse(call: Call<Int?>, response: Response<Int?>) {
                    updateChatroom(-1)
                }
                override fun onFailure(call: Call<Int?>, t: Throwable) {}
            })
        }
        return super.onContextItemSelected(item)
    }
    override fun onResume() {
        (activity as MainActivity).setAppBarTitle("Nhắn tin")
        super.onResume()
    }

    fun updateChatroom(chatroomID: Int) {
        Database.getInstance().getAPI()
            .getChatRoomByUserId(CurrentUser.getInstance().getUser()!!.getUserId())
            .enqueue(object : Callback<List<MyChatRoom>?> {
                override fun onResponse(
                    call: Call<List<MyChatRoom>?>,
                    response: Response<List<MyChatRoom>?>
                ) {
                    (requireActivity().application as LinkerApplication).clearChatRoom()
                    chatRoomList.clear()
                    val chatRooms = response.body()
                    if (chatRooms != null) {
                        for (chatRoom in chatRooms) {
                            if (chatRoom.getId() == chatroomID) {
                                chatRoom.setHighLight(1)
                            }
                            chatRoomList.add(chatRoom)
                            (requireActivity().application as LinkerApplication).addChatRoom(chatRoom)
                        }
                    }
                    chatRoomList.sortWith { o1, o2 ->
                        (o2.getHighLight()!!).compareTo(o1.getHighLight()!!)
                    }
                    listAdapter.notifyDataSetChanged()
                }

                override fun onFailure(call: Call<List<MyChatRoom>?>, t: Throwable) {
                }
            })
    }

}
