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
import com.example.linker_kotlin.R
import com.example.linker_kotlin.Service.Database.Database
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatFragment : Fragment() {

    private val listAdapter: ChatListAdapter? = null
    private val chatRoomList: List<MyChatRoom>? = null
    private val listView: ListView? = null
    private val circleImageView: CircleImageView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View?
        val view2: View?
        try{
            val listChatRoom = ArrayList<MyChatRoom>()
            view = inflater.inflate(R.layout.fragment_chat,container, false)
            view2 = inflater.inflate(R.layout.chat_fragment_item,container,false)
            var circleImageView = view2.findViewById<CircleImageView>(R.id.chat_group_profile_pic)

            var currentUser = CurrentUser.getInstance().getUser()
            var listView = view.findViewById<ListView>(R.id.chat_fragment_list_view)
            var listAdapter = ChatListAdapter()

            registerForContextMenu(listView)

            Database.getInstance().getAPI().getChatRoomByUserId(currentUser?.getUserId()).enqueue(object : Callback<List<MyChatRoom>> {
                    override fun onResponse(call: Call<List<MyChatRoom>>,response: Response<List<MyChatRoom>>) {
                        (requireActivity().application as LinkerApplication).clearChatRoom()
                        val chatRooms : List<MyChatRoom>? = response.body()
                        if (chatRooms != null) {
                            for (chatRoom in chatRooms){
                                listChatRoom.add(chatRoom)
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
                    val clickedChatroom: MyChatRoom = listChatRoom.get(position)
                    val i = Intent(activity, ChatActivity::class.java)
                    i.putExtra("id", clickedChatroom.getId())
                    i.putExtra("displayName", clickedChatroom.getPromientMember()?.getDisplayName())
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
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateContextMenu(menu: ContextMenu,v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val menuInflater = requireActivity().menuInflater
        menuInflater.inflate(R.menu.long_click_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.delete_option) {
            val info = item.menuInfo as AdapterContextMenuInfo
            val chatRoom: MyChatRoom? = listAdapter?.getChatRoomPosition(info.position)
            Database.getInstance().getAPI().deleteChatroomByID(chatRoom?.getId()).enqueue(object : Callback<Int?> {
                override fun onResponse(call: Call<Int?>, response: Response<Int?>) {
                    updateChatroom(-1)
                }
                override fun onFailure(call: Call<Int?>, t: Throwable) {}
            })
        }
        return super.onContextItemSelected(item)
    }
    override fun onResume() {
        (activity as MainActivity?)!!.setAppBarTitle("Chat")
        super.onResume()
    }

    fun updateChatroom(chatroomID: Int) {
        Database.getInstance().getAPI().getChatRoomsByUserID(CurrentUser.getInstance().getUser()!!.getUserId())
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
                                chatRoom.setHighlight(1)
                            }
                            chatRoomList.add(chatRoom)
                            (requireActivity().application as LinkerApplication).addChatRoom(chatRoom)
                        }
                    }
                    chatRoomList.sort(Comparator<MyChatRoom> { o1, o2 ->
                        Integer.compare(
                            o2.getHighlight(),
                            o1.getHighlight()
                        )
                    })
                    listAdapter!!.notifyDataSetChanged()
                }

                override fun onFailure(call: Call<List<MyChatRoom>?>, t: Throwable) {
                }
            })
    }


}