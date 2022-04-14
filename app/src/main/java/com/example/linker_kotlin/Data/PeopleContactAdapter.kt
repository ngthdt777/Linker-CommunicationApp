package com.example.linker_kotlin.Data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.linker_kotlin.R
import com.example.linker_kotlin.Service.ChatService
import com.example.linker_kotlin.Service.Database.Database
import com.example.linker_kotlin.Util.Utility
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import org.linphone.core.ChatRoom
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class PeopleContactAdapter(context: Context, searchItemList: MutableList<User>) :
    RecyclerView.Adapter<PeopleContactAdapter.SearchItemViewHolder>(), Filterable {
    private val searchItemList: List<User?>
    private lateinit var searchItemListFull: List<User?>
    private val context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchItemViewHolder {
        val v: View =
            LayoutInflater.from(parent.context).inflate(R.layout.people_item, parent, false)
        return SearchItemViewHolder(v)
    }

   override fun onBindViewHolder(holder: SearchItemViewHolder, position: Int) {
        val currentItem = searchItemList[position]
        Picasso.get().load(currentItem!!.getProfilePicture()).into(holder.profilePicture)
        holder.personName.text = currentItem.getDisplayName()
        holder.addBtn.setOnClickListener {
            Database.getInstance().getAPI().addSingleChatRoom().enqueue(object : Callback<Int?> {
                override fun onResponse(call: Call<Int?>,response: Response<Int?>) {
                    val id = response.body()!!
                    val local = UserInChatModel(id, CurrentUser.getInstance().getUser()?.getUserId()!!)
                    val remote = UserInChatModel(id, currentItem.getUserId()!!)
                    Database.getInstance().getAPI().addUserInChat(local)
                        .enqueue(object : Callback<Int?> {
                            override fun onResponse(
                                call: Call<Int?>,
                                response: Response<Int?>
                            ) {
                                Database.getInstance().getAPI().addUserInChat(remote)
                                    .enqueue(object : Callback<Int?> {
                                        override fun onResponse(
                                            call: Call<Int?>,
                                            response: Response<Int?>
                                        ) {
                                            val room = ChatService.getInstance().getChatRoom(remote.userID)
                                            ChatService.getInstance().sendMessage(
                                                "*#Refresh_chatroomList#*!!",
                                                room!!,
                                                0,
                                                "updateChatroomList"
                                            )
                                        }

                                        override fun onFailure(
                                            call: Call<Int?>,
                                            t: Throwable
                                        ) {
                                        }
                                    })
                            }

                            override fun onFailure(
                                call: Call<Int?>,
                                t: Throwable
                            ) {
                            }
                        })
                }

                override fun onFailure(call: Call<Int?>, t: Throwable) {}
            })
            //(context as AddFriendActivity).removeContact(currentItem.getUserId())
            Toast.makeText(context, "Added new contact!", Toast.LENGTH_LONG).show()
        }
    }

    override fun getItemCount(): Int {
        return searchItemList.size
    }

    fun clearData() {
        searchFiler.filter("")
    }

    override fun getFilter(): Filter {
        return searchFiler
    }

    private val searchFiler: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filterList: MutableList<User?> = ArrayList()
            if (constraint.length == 0 || constraint == "") {
                filterList.addAll(searchItemListFull)
            } else {
                val filter = constraint.toString().lowercase(Locale.getDefault()).trim { it <= ' ' }
                for (searchItem in searchItemListFull) {
                    if (searchItem!!.getDisplayName()!!.lowercase(Locale.getDefault()).contains(filter)) {
                        filterList.add(searchItem)
                    }
                }
            }
            val results = FilterResults()
            results.values = filterList
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            searchItemList.clear()
            searchItemList.addAll(results.values as List<User>)
            notifyDataSetChanged()
        }
    }

    class SearchItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profilePicture: CircleImageView
        var personName: TextView
        var addBtn: ImageButton

        init {
            profilePicture = itemView.findViewById(R.id.profile_image)
            personName = itemView.findViewById(R.id.contact_name)
            addBtn = itemView.findViewById(R.id.people_add_contact_icon)
        }
    }

    init {
        this.searchItemList = searchItemList
        searchItemListFull = ArrayList<User>(searchItemList)
        this.context = context
    }
}
